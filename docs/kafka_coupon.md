# Kafka를 이용하여 선착순 쿠폰 발급 비즈니스 프로세스 개선

## 선착순 쿠폰 발급 프로세스
<img width="1197" alt="Image" src="https://github.com/user-attachments/assets/79f72ced-28d1-4523-be47-218a9204f2c3" />


```mermaid
sequenceDiagram
    User->>Coupon: 쿠폰 발급 요청
    Coupon->>Redis: 중복 발급 확인
    Coupon->>EventListener: 쿠폰 예약 이벤트 발행
    EventListener->>DB: CouponOutbox에 이벤트 저장
    Producer->>Kafka: 쿠폰 발급 메시지 발행(topic: coupon-reserved)
    Kafka->>Consumer: 쿠폰 발급 메시지 수신
    Consumer->>Coupon: 쿠폰 발급
    Coupon->>DB: 발급된 쿠폰 저장
```

1. 유저가 쿠폰 발급 요청을 하면 Redis의 발급된 userId가 들어있는 SET을 조회하여 중복 발급 방지
2. 중복되지 않은 유저임을 확인하면 SpringApplicationEvent 발행
   - BEFORE_COMMIT: CouponOutbox에 이벤트 기록(eventStatus: `INIT`)
   - AFTER_COMMIT: 카프카 메시지 발행
3. 발행된 카프카 메시지를 수신하여 쿠폰 발급 진행하고 CouponOutbox의 이벤트 상태를 `SEND_SUCCESS`로 변경
4. 주기적으로 CouponOutbox에 상태가 `INIT`인 이벤트를 조회하여 카프카 메시지 재발행

## Kafka 구성
<img width="854" alt="Image" src="https://github.com/user-attachments/assets/af6da4b8-d708-4523-b573-a6d8fc4b90c9" />
