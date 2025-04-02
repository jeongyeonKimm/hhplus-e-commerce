## ERD

---

![Image](https://github.com/user-attachments/assets/df2f6280-5d21-4f74-84a6-e73b6dbb9c41)

### 테이블 간의 관계

---

### 1. User - Order (1:N)

- 한 명의 사용자는 여러개의 주문 생성 가능

### 2. Orders - OrderItem (1:N)

- 한 개의 주문에 여러개의 주문 상품 포함 가능

### 3. Product - OrderItem (1:N)

- 한 개의 상품은 여러 주문 상품에 포함 가능

### 4. User - UserCoupon (1:N)

- 한 명의 사용자는 여러 개의 쿠폰 보유 가능

### 5. Coupon - UserCoupon (1:N)

- 하나의 쿠폰은 여러 사용자에게 발급 가능

### 6. User - Point (1:1)

- 한 명의 사용자는 한 개의 포인트 보유

### 7. Point - PointHistory (1:N)

- 한 개의 포인트는 여러 개의 포인트 이력에 남을 수 있음

### 8. Orders - UserCoupon (1:1)

- 한 개의 주문에 사용자가 보유한 쿠폰 0개 혹은 1개 사용 가능

### 9. BestSeller

- 상품의 판매량 데이터 저장 테이블
- BestSeller 테이블의 판매량을 기준으로 상위 5개 상품이 인기 상품 

