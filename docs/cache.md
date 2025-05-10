## 캐시
### 캐시란?
- 자주 사용되거나 반복적으로 사용되는 데이터를 빠르게 접근할 수 있도록 임시로 저장하는 저장소
- 일반적으로 RAM에 위치하여, Disk나 DB 보다 빠른 응답 속도 제공

### 캐시 사용 이유
- 성능 향상(Latency 감소): DB나 외부 API 호출 없이 캐시에서 데이터를 즉시 반환함으로써 응답 속도를 줄일 수 있음
- 트래픽 부하 분산: 동일한 요청에 대해 매번 DB를 조회하는 대신 캐시를 사용하게 되면 DB 부하가 감소됨
- 비용 절감: DB 리소스를 효율적으로 사용하게 되어 인프라 비용 절감
- 가용성 향상: DB 장애 시에도 최소한의 서비스 지속 가능

### 캐시의 분류
- Local Cache: 애플리케이션 내부 메모리에 저장(e.g. Caffeine, Ehcache, ...), 속도는 빠르나 인스턴스간 데이터 공유가 어려움
- Distributed Cache: 외부에 위치한 공유 캐시 서버 사용(e.g. Redis, Memcached, ...), 다수의 서버 간 캐시 공유 가능

### 캐시 전략
| 전략 이름                    | 설명                                            |
|--------------------------|-----------------------------------------------|
| Read-through             | 항상 캐시에서 데이터를 조회하고, 캐시 미스시 DB 조회 후 캐시에 저장      |
| Write-through            | DB를 업데이트 할 때 마다 캐시도 함께 업데이트                   |
| Write-behind(Write-back) | 캐시에 먼저 업데이트 하고, 건수나 특정 시간 간격으로 비동기적으로 DB에 업데이트 |
| Cache invalidation       | DB 업데이트할 때마다 캐시에서 데이터 삭제                      |

### 캐시 설계 시 고려사항
- 데이터의 정합성(Consistency): 캐시된 데이터가 실제 DB의 최신 데이터와 얼마나 일치해야 하는가?
- 변경 빈도(Frequency of Update): 자주 바뀌는 데이터는 캐시에 적합하지 않거나 TTL을 짧게 설정해야 함
- 접근 패턴(Access Pattern): 같은 키로 반복 접근이 일어나는 경우 캐시 효율 높음
- Key 설계: 조건 조합이 많아지는 조회(e.g. 필터, 정렬)는 Key explosion 위험 존재
- Stale 데이터 처리: 캐시 TTL 만료 전 변경된 DB 내용을 어떻게 반영할지 고민해야 함(만료 or 명시적 삭제 or 캐시 무효화)

### 캐시의 한계와 주의점
- Stale 데이터 리스크: TTL 동안 DB와 불일치한 오래된 데이터가 사용자에게 보여질 수 있음
- Key Explosion: 필터/페이지 조합이 많은 경우 캐시 키 수가 기하급수적으로 증가할 수 있음
- 복잡한 무효화 로직: 데이터가 변경될 떄 연관된 캐시를 모두 삭제하거나 갱신하는 것이 어려울 수 있음
- Failover 시 장애 위험: Redis 등 캐시 서버 장애 시 Fallback이 제대로 안되어 전체 서비스가 느려지거나 오류 발생 가능

---
## 이커머스 캐시 적용 후보
| 항목                  | 인기 상품 조회 (`/bestsellers`)                        | 상품 목록 조회 (`/products`)                            |
|---------------------|-------------------------------------------------------|---------------------------------------------------------|
| **데이터 변화 빈도**   | 낮음 (1일 단위 갱신)                             | 높음 (상품 추가/수정/삭제, 재고/가격 변화 등)               |
| **접근 패턴**         | 대부분 동일한 키 (`best`)로 조회                           | 다양한 쿼리 조건 (카테고리, 검색어, 필터 등)                |
| **핫데이터 여부**     | 명확함 (상위 N개 인기 상품)                                | 조건에 따라 달라짐 (일부 인기 카테고리는 핫할 수 있음)         |
| **캐시 적합성**       | 매우 적합 (읽기 비중 높고 변경 적음)                       | 조건부 적합 (읽기 비중 높지만 쿼리 다양)                   |
| **캐시 전략 예시**     | - Redis + TTL 24h<br>- Read-through 캐시                   | - 카테고리별 일부 캐싱 (ex. `hot`, `new`)<br>- 캐시 미스 시 DB 조회 |
| **키 구성 방식**       | 고정 키 (`best`)                                          | 동적 키 (ex. `products:category=shoes&page=1`)           |
| **캐시 무효화 전략**  | 스케줄러에 의한 주기적 갱신 (ex. 하루 1회)                    | 실시간 무효화 어려움, 갱신 빈도 낮은 쿼리만 캐싱 추천           |
| **주의할 점**         | TTL 동안 stale 데이터 가능성 있음                           | 조건별 조합 수 많음 → Redis 메모리 부담 가능성             |

인기 상품 조회와 상품 목록 조회에 캐시 적용을 고려해보았을 때,
데이터의 변화 빈도가 낮고, 읽기 비중이 높은 `인기 상품 조회`에 대해서는 캐시를 적용하고, 비교적 데이터 변화 비중이 높고 데이터의 양이 많은 `상품 목록 조회`에 대해서는 캐시를 적용하지 않기로 결정  

## 인기 상품 조회에 캐시 적용

인기 상품 조회는 최근 3일 판매량을 기준으로 상위 5개의 상품 정보를 반환
<br />
현재 인기 상품은 1시간 마다 판매량을 집계하여 BestSeller 테이블에 적재하고, 인기 상품 조회 요청이 오면 최근 3일치를 집계하여 상위 5개의 상품 반환하도록 구현되어 있음.


### 캐시 적용 방법
1. Read-through 전략을 취하여 유저는 항상 캐시에서 데이터를 읽어옴
    ```java
    @Cacheable(value = "bestSellers", key = "'best'", cacheManager = "redisCacheManager")
    public BestSellerDto getBestSellers() {
    List<BestSeller> bestSellers = bestSellerRepository.getBestSellers();
    return BestSellerDto.of(bestSellers);
    }
    ```
2. 스케줄러를 이용해 매일 23:50에 인기 상품 데이터 갱신(스케줄러 주기는 24시간)
    ```java
    @Scheduled(cron = "0 50 23 * * *")
    public void preloadBestSellersCache() {
        BestSellerService proxy = applicationContext.getBean(BestSellerService.class);
        proxy.refreshBestSellers();
        log.info("[BestSellerScheduler] 3일간 인기 상품 캐싱 완료");
    }
   ```
   ```java
   @CachePut(value = "bestSellers", key = "'best'", cacheManager = "redisCacheManager")
    public BestSellerDto refreshBestSellers() {
        List<BestSeller> bestSellers = bestSellerRepository.getBestSellers();
        return BestSellerDto.of(bestSellers);
    }
   ```
3. 이 때 캐시의 TTL은 25시간으로 설정하여 새로운 인기 상품 데이터가 캐시에 적재되는 시간에도 기존에 캐시된 데이터는 유효하므로 유저는 캐시에서 데이터를 조회할 수 있음.
    ```java
   @Bean
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory);

        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofHours(25));
        builder.cacheDefaults(configuration);
        return builder.build();
    }
   ```

### 성능 비교
100명의 사용자가 30초 동안 인기 상품 조회 요청
- 캐시 적용 전
<img width="1284" alt="Image" src="https://github.com/user-attachments/assets/fd2ce8eb-28f6-4bf0-9549-7871d02574f4" />
    - TPS: 254req/sec

- 캐시 적용 후
<img width="1257" alt="Image" src="https://github.com/user-attachments/assets/c82138f0-3fdd-4af6-9d13-a33cb2237bf4" />
    - TPS: 13437req/sec

### 결론
> 캐시 적용 전 `TPS 254req/sec`에서 캐시 적용 후 `TPS 13437req/sec`로 캐시 적용을 통해 **약 53배** 성능 향상됨

