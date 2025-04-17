# 인덱스

## 1. 인덱스란

### 인덱스란
> 데이터베이스 테이블의 검색 속도를 빠르게 하기 위한 자료 구조

### 인덱스의 동작 원리 (B-Tree 기반)
대부분의 RDBMS는 B-Tree 인덱스 사용

#### B-Tree 인덱스란
- 균형 이진 트리 구조에서 발전한 자료구조
- 트리의 높이를 낮게 유지해 검색, 삽입, 삭제를 log(N) 시간에 수행

#### 동작 방식
- 인덱스는 특정 컬럼의 값을 정렬하여 저장
- 루트 노드부터 시작해서 값을 탐색
- 리프 노드에는 실제 테이블의 row 위치(row pointer)가 저장되어 있음
- 인덱스를 통해 빠르게 row 위치를 찾아 해당 데이터 읽음

### 인덱스의 장점
- WHERE, JOIN, ORDER BY, GROUP BY 성능 향상
- 자동으로 정렬된 결과를 가져오므로 ORDER BY 비용 감소
- UNIQUE 인덱스를 통해 중복 방지

### 인덱스의 단점
- INSERT/UPDATE/DELETE를 할 떄 인덱스를 수정해야 하므로 오버헤드 발생
- 인덱스를 저장하는 별도의 공간 필요
- 너무 많은 인덱스는 오히려 성능 저하 초래(특히 쓰기 작업)

### 인덱스의 종류
- B-Tree 인덱스: 가장 일반적인 인덱스, WHERE 절의 등호, 부등호, BETWEEN, IN 등에 효과적
- 복합 인덱스: 여러 컬럼을 묶어서 만든 인덱스
- UNIQUE 인덱스: 값의 중복을 허용하지 않음(중복되면 INSERT/UPDATE 실패)
- FULLTEXT 인덱스: 문자열 전문 검색에 사용
- HASH 인덱스: 등호 조건에만 빠르고, 범위 검색은 느림
- Spatial 인덱스: 공간 좌표 데이터를 위한 인덱스

### 인덱스 설계 시 고려사항
- 카디널리티가 높은 컬럼을 인덱스로 만든다.
- WHERE절에 자주 사용되는 컬럼을 인덱스로 만든다.
- JOIN 조건에 자주 사용되는 컬럼을 인덱스로 만든다.
- ORDER BY, GROUP BY에도 인덱스가 유리할 수 있다.
- 복합 인덱스는 컬럼 순서가 중요하다.

---

## 2. 조회가 느릴 것으로 예상되는 기능

### 2-1. 인기 상품 목록 조회

#### 조회쿼리

```sql
SELECT product_id, 
       product_title AS title,
       product_description AS description,
       product_price AS price,
       product_stock AS stock,
       SUM(sales) AS total_sales
FROM best_seller
WHERE created_at >= NOW() - INTERVAL 3 DAY
GROUP BY product_id
ORDER BY total_sales DESC
LIMIT 5;
```

- 1시간 마다 스케줄러가 돌면서 상품별로 판매량을 집계하여 best_seller 테이블에 insert
- 스케줄러를 통해 매일 새벽 3일이 지난 데이터 삭제
- 인기 상품 목록 조회 시 1시간씩의 판매량을 상품별로 집계하여 판매량 상위 5개의 상품 정보와 최근 3일 판매량 조회

#### 병목 포인트

- `created_at >= NOW() - INTERVAL 3 DAY` 인덱스를 타지 못할 때
- `GROUP BY product_id` 연산 자체의 과부하
- `ORDER BY total_sales DESC` 정렬 비용

#### 해결 방법
- `product_id`, `created_at`, `sales`에 복합 인덱스 설정
```sql
CREATE INDEX idx_best_seller_product_created_sales 
    ON best_seller(product_id, created_at, sales);
```

- `SUM(product_sales)`은 `GROUP BY` 이후에 계산되기 때문에 정렬하려면 그룹 결과를 메모리에 적재 필요 -> filesort 발생
- 이는 정렬하는 그룹이 수천 개만 되어도 부담이 커짐
- 따라서 한번 조회된 인기 상품을 **cache table**에 저장하여 조회 성능 향상 필요

### 2-2. 상품 목록 조회

#### 조회 쿼리

```sql
SELECT *
FROM product
```

#### 병목 포인트
- Full Scan 대상

#### 해결 방법
- Full Scan이 필요하기 때문에 인덱스를 설정하는 것이 큰 도움이 되지 않음
- 추후 카테코리가 추가되거나 필터링 조건(가격 등)이 추가됨녀 해당 조건들로 인덱스를 설정하게 되면 조회 성능 향상
- 페이징 처리를 통한 조회 성능 향상 기대

### 2-3. 사용자 보유 쿠폰 조회

#### 조회 쿼리

```sql
SELECT *
FROM user_coupon
WHERE user_id = ?
```

#### 해결 방법

- `user_id`에 인덱스 설정
```sql
CREATE INDEX idx_user_coupon_user ON user_coupon(user_id);
```

### 2-4. 재고 조회(주문)

#### 조회 쿼리

```sql
SELECT stock
FROM product
WHERE product_id = ?
```

#### 해결 방법

- `product_id`는 PK이기 때문에 인덱스를 따로 설정할 필요 없음

### 2-5. 잔액 조회(결제)

#### 조회 쿼리

```sql
SELECT *
FROM point
WHERE user_id = ?
```

#### 해결 방법

- `user_id`에 인덱스 설정
```sql
CREATE INDEX idx_point_user ON point(user_id);
```

### 2-6. 주문 정보 조회(결제)

#### 조회 쿼리

```sql
SELECT *
FROM order
WHERE order_id = ?
```

#### 해결 방법

- - `order_id`는 PK이기 때문에 인덱스를 따로 설정할 필요 없음
