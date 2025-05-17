# Redis Design

## Ranking Design(인기 상품 조회)

### 설계 방식

1. 주문이 성공하면(트랜잭션 커밋 완료 후) 레디스의 Sorted Set에 각 상품별 판매량 저장
   - key: sales:daily:{yyyyMMdd}
   - score: 1일 누적 판매량
   - value: productId:{productId}
   - TTL: 30일(추후 월간 데이터 집계 기능이 생긴다는 것을 고려하여 30일로 설정)
2. 10분마다 스케줄러가 실행되면서 최근 3일치 인기 상품 집계 및 TOP5 캐싱
   1. 3일동안 판매된 상품 수량 집계
   2. 판매량이 많은 순으로 5개 추출
   3. TOP5에 대한 상품 정보 RDB에서 조회
   4. 조회된 데이터 캐싱

### Redis Sorted Set 적용 이유

1. 조회 성능 향상
   - 인기 상품 조회는 사용자 접근이 많은 API
   - Redis는 메모리 기반이기 때문에 RDB에 비해 조회 속도가 빠름
2. RDB 부하 분산
   - 인기 상품은 매 요청마다 정렬 및 집계 쿼리가 필요
   - 많은 유저가 동시에 접속하는 상황에서 정렬, 집계 쿼리가 반복되면 DB에 부하 집중
   - Redis의 Sorted Set에 판매량을 score로 정렬해고 조회만 하면 DB 부하 줄어듦

## Asynchronous Design(선착순 쿠폰 발급)

### 설계 방식

1. 쿠폰 발급 요청 시 레디스의 Sorted Set에 요청한 userId 저장 <br/>coupon:issued:{couponId}을 key로 가지는 Set에 존재하지 않으면 발급 요청 저장
    - key: coupon:request:{couponId}
    - score: 발급 요청 시간
    - value: userId:{userId}
2. 30초 마다 스케줄러가 실행되면서 BATCH_SIZE 만큼씩 Sorted Set에 있는 쿠폰 발급 요청 처리
   1. 쿠폰 재고를 기준으로 사용자에게 쿠폰 발급
   2. 발급된 요청은 Set에 저장
      - key: coupon:issued:{couponId}
      - value: userId:{userId}
   3. 재고가 충분함에도 쿠폰 발급이 실패한 경우 Set에 저장해두고 추후 재처리
      - key: coupon:fail:{couponId}
      - value: userId:{userId}
   4. 발급에 성공한 사용자 수 만큼 DB에서 재고 차감

### Redis Sorted Set, Set 적용 이유

1. 선착순 정렬 보장
    - 요청 시간과 사용자 ID를 각각 score, value에 저장
    - Sorted Set은 score를 기준으로 정렬되기 때문에 선착순 보장됨
2. 중복 발급 방지
   - Set은 데이터를 중복해서 저장할 수 없음
   - 쿠폰 발급이 된 사용자 ID를 저장하고, 발급 요청시 확인함으로써 중복 발급 방지
