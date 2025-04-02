## 시퀀스 다이어그램

---

### 1️⃣ 잔액 충전
![Image](https://github.com/user-attachments/assets/2309cb4a-e471-4ffb-8393-eb80241a5393)

- 사용자 ID, 충전 금액을 받아 잔액 충전
- 충전한 내역 저장

### 2️⃣ 잔액 조회
![Image](https://github.com/user-attachments/assets/4c69835d-f67b-48d1-a0d8-0aee880f5d93)

- 사용자 ID를 이용해 사용자의 보유 잔액 조회

### 3️⃣ 상품 조회
![Image](https://github.com/user-attachments/assets/63895a8f-19f2-478b-9d5f-f9bb34ec1edd)

- 전체 상품 목록 조회(상품 ID, 상품명, 가격, 재고)

### 4️⃣ 선착순 쿠폰 발급
![Image](https://github.com/user-attachments/assets/a201c721-09f7-466b-816f-301fab2acd4a)

- 사용자 ID, 발급 받을 쿠폰 ID를 받아 선착순 쿠폰 발급
- 쿠폰의 잔여 수량이 0일 경우 쿠폰 발급 불가
- 이미 발급 받은 쿠폰인 경우 중복 발급 불가

### 5️⃣ 보유 쿠폰 조회
![Image](https://github.com/user-attachments/assets/77e879c8-0fb9-4ed7-9b6b-112ee2a06ac9)

- 사용자 ID를 이용해 사용자의 보유 쿠폰 목록 조회

### 6️⃣ 주문
![Image](https://github.com/user-attachments/assets/a39a4444-f2e4-46da-af21-893029dcfede)

- 사용자 ID, 적용할 쿠폰 ID, 주문할 상품(상품 ID, 수량)의 목록을 입력받아 주문
- 재고가 부족한 상품이 있는 경우 주문 실패
- 유효한 쿠폰이면 전체 주문 금액에 쿠폰을 적용
- 주문이 완료되면 주문 상태는 NOT_PAID

### 7️⃣ 결제
![Image](https://github.com/user-attachments/assets/c1116e62-1b2b-4946-be16-de5875727521)

- 주문 ID를 입력 받아 결제
- 잔액이 주문 금액보다 적게 있을 경우 결제 실패
- 잔액을 차감하고, 주문 상태를 PAID 상태로 업데이트
- 외부 데이터 플랫폼으로 주문 정보 전송

### 8️⃣ 결제 실패 후 주문 만료 처리 스케줄러
![Image](https://github.com/user-attachments/assets/31b1aca9-bdab-477f-9114-62a67d544dfb)

- 결제가 되지 않은 채로 5분이 지난 주문은 주문 상태 EXPIRED 처리
- 주문을 하면서 차감된 재고 롤백
- 쿠폰을 사용한 경우 쿠폰 사용 상태를 false 처리

### 9️⃣ 인기 상품 조회
![Image](https://github.com/user-attachments/assets/42a3d888-c564-4007-99ae-793281b34f9a)

- 판매량을 기준을 상위 5개 상품 반환

### 🔟 인기 상품 추출 스케줄러
![Image](https://github.com/user-attachments/assets/3d5c61b2-511b-4ad3-a525-b4d260b8d020)

- 주문 완료된 상품을 기준으로 각 상품의 일별 주문량 저장
