# MSA 전환 설계 보고서

## 1. 개요

현재 이커머스 서비스는 모놀리식 아키텍처로 운영되고 있으며, 하나의 배포 단위에 모든 도메인이 포함되어 있다. 
사용자 증가와 트래픽 확장에 따라, 서비스의 확장성과 유지보수성 확보를 위해 MSA(Microservice Architecture) 형태로의 전환이 요구된다.

## 2. 도메인 기반 마이크로서비스 분리 설계

### 2.1 도메인 분리 기준

도메인과 DB, 배포 단위를 기준으로 다음과 같이 마이크로서비스 단위를 분리한다.

<img width="925" alt="Image" src="https://github.com/user-attachments/assets/c9372b5e-b051-41d8-b4a2-b3642101c07b" />

| 마이크로서비스                   | 주요 책임      | 주요 데이터          | 주요 기능              |
|---------------------------|------------|-----------------|--------------------|
| **User Service**          | 사용자 계정 관리  | 사용자 정보          | 회원가입, 로그인, 인증 등    |
| **Product Service**       | 상품 정보 관리   | 상품, 재고          | 상품 조회, 등록, 재고 차감   |
| **Order Service**         | 주문 관리      | 주문, 주문상품        | 주문 생성, 주문 상태 변경    |
| **Payment Service**       | 결제 처리      | 결제, 결제 로그       | 포인트 충전, 차감         |
| **Coupon Service**        | 쿠폰 발급 및 사용 | 쿠폰, 사용자 쿠폰      | 쿠폰 생성, 발급, 사용처리    |
| **Point Service**         | 포인트 관리     | 사용자 포인트, 포인트 이력 | 포인트 충전, 차감, 잔액 조회  |
| **Sales Ranking Service** | 상품 판매량 집계  | 상품별 판매량         | Redis 기반 상품 판매량 집계 |
| **Best Seller Service**   | 인기 상품 조회 | 캐싱된 인기 상품       | 판매량 기준 TOP5        |


## 3. 트랜잭션 처리의 한계

### 3.1 분산 트랜잭션 불가

MSA에서는 각 서비스가 독립된 데이터베이스를 가지기 때문에, 모놀리식 환경에서 가능했던 **단일 트랜잭션 관리**가 불가능하다. 

- 주문 생성 이후 재고 차감, 포인트 차감, 쿠폰 사용, 결제 완료까지의 흐름 <br/>
- 이 과정에서 하나의 단계라도 실패하면, 전체를 롤백해야 하지만 분산된 트랜잭션에서는 불가능


### 3.2 실패 시 일관성 붕괴 위험

- 주문은 생성되었는데 결제 실패 → 유령 주문 발생

- 재고는 차감되었는데 주문 취소 → 재고 손실

## 4. 트랜잭션 처리 해결 방안

### 4.1 Saga 패턴 도입

비동기 이벤트 기반의 Saga 패턴을 도입하여 분산된 트랜잭션을 보상 트랜잭션으로 대체한다.

#### Choreography 기반 Saga (이벤트 기반 분산 흐름)

- Order Service: 주문 생성 → `OrderCreatedEvent` 발행

- Product Service: 재고 차감 → `ProductDuductedEvent` 발행

- Payment Service: 결제, 포인트/쿠폰 처리 → `PaymentCompletedEvent` 발행

- Order Service: 주문 상태 변경 (PAID)

- 중간 단계 실패 시 `OrderCanceledEvent`, `ProductRollbackEvent` 등 보상 이벤트 발행

```java
public class OrderService {
    
    @Transactional
    public void createOrder(...) {
        // 주문 생성
        orderRepository.save(order);
        
        // 주문 완료 이벤트 발행
        eventPublisher.publish(OrderEvent.OrderCreatedEvent(...));
    }
    
    ...
}

public class ProductOrderEventListener {
    ...

    @Async
    @EventListener
    public void handle(OrderEvent.OrderCreatedEvent event) {
        try {
            재고_차감();
        } catch (...) {
            // 재고 차감 실패 이벤트 발행
            eventPublisher.publish(ProductEvent.StockDeductFailedEvent(...));
        }
    }

    @Async
    @EventListener
    public void handle(ProductEvent.StockDeductFailedEvent event) {
        재고_롤백();
        주문_상태_변경();
    }
}
```

```java
public class PaymentService {
    ...
    
    @Transactional
    public void pay(...) {
        try {
            결제();

            // 결제 완료 이벤트 발행
            eventListener.publish(PaymentEvent.PaymentCompleted(...));
        } catch(...) {
            // 결제 실패 이벤트 발행
            eventListener.publish(PaymentEvent.PaymentFailed(...));
        }
    }
}

public class ProductPaymentEventListener {
    @Async
    @EventListener
    public void handle(PaymentEvent.PaymentFailed event) {
        재고_롤백();
    }
}

public class CouponPaymentEventListener {
    
    @Async
    @EventListener
    public void handle(PaymentEvent.PaymentFailed event) {
        쿠폰_롤백();
    }
}

public class OrderPaymentEventListener {
    @Async
    @EventListener
    public void handle(PaymentEvent.PaymentCompleted event) {
        // 주문 상태 PAID로 변경
        주문_상태_변경();
    }
}

public class DataPaymentEventListener {
    @Async
    @TransactionalEventListeners(AFTER_COMMIT)
    public void handle(PaymentEvent.PaymentCompleted event) {
        데이터_전송();
    }
}
```

#### ✔️ 트랜잭션 보상 예시

| 실패 단계    | 보상 트랜잭션                 |
|----------|-------------------------|
| 재고 차감 실패 | 주문 취소                   |
| 결제 실패    | 재고 복원, 주문 취소 |

### 4.2 이벤트 기반 통신 설계

- Kafka, RabbitMQ 등의 메시지 브로커를 도입하여 서비스 간 이벤트를 안정적으로 전달

- 초기에는 Spring ApplicationEventPublisher로 내부 동기 이벤트 처리 후 외부 메시징으로 점진적 전환 가능

### 4.3 데이터 정합성 확보 전략

- 각 서비스는 자신의 데이터 일관성만 책임

- 글로벌 일관성은 최종 일관성(Eventual Consistency) 기반으로 설계

- 주문 상태는 상태 전이(상태머신)로 엄격하게 관리하여 중간 실패를 허용하지 않음

## 5. 결론

MSA 전환은 시스템의 확장성과 유지보수성을 개선할 수 있는 방향이지만, 
분산 트랜잭션 환경에서 데이터 정합성과 장애 대응을 위해 Saga 패턴과 이벤트 기반 통신 체계를 함께 도입하는 것이 필수적이다. 

