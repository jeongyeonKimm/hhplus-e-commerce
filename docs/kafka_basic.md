# Apache Kafka 기초 개념 및 실습

## Apache Kafka란?

Apache Kafka는 대용량의 실시간 데이터 스트리밍을 처리하기 위한 **분산형 메시징 시스템**이다. 
주로 **로그 수집**, **이벤트 소싱**, **실시간 분석**, **MSA 간 통신** 등에 활용된다.

## 핵심 개념

### 1. Producer
- 메시지를 생성하여 Kafka로 발행하는 주체 (예: 결제 완료 이벤트 발행)

### 2. Consumer
- Kafka로부터 메시지를 구독하고 처리하는 주체 (예: 결제 성공시 포인트 적립)

### 3. Topic
- 메시지가 전송되는 카테고리 
- `order-created`, `payment-completed` 등으로 분류
- 레코드(record): 프로듀서가 보낸 데이터, timestamp가 포함되어 있음
- 컨슈머가 가져가도 데이터를 삭제하지 않음

### 4. Partition
- Topic은 여러 파티션으로 나뉘며, 파티션 단위로 메시지가 분산 저장 
- 컨슈머의 개수를 늘림과 동시에 파티션 개수도 늘리면서 병렬 처리 성능 향상에 도움을 줌
- 프로듀서, 컨슈머는 반드시 리더 파티션과 직접 통신
- 팔로워 파티션들은 리더 파티션의 오프셋을 확인하여 현재 자신이 가지고 있는 오프셋과 차이가 나는 경우 리더 파티션으로부터 데이터를 가져와서 자신의 파티션에 저장
- 파티션 개수를 줄이는 것은 불가능

### 5. Offset
- Consumer가 메시지를 읽을 때 사용하는 위치 정보 
- 각 파티션마다 관리된다.
- `__consumer_offsets` 토픽에 저장된다.

### 6. Broker
- Kafka 서버 인스턴스를 의미하며, 여러 개의 Broker가 클러스터를 구성
- 서버 1대로도 기본 기능이 실행되지만 데이터를 안전하게 보관하고 처리하기 위해 3대 이상의 브로커 서버를 1개의 클러스터로 묶어서 운영
- 브로커가 다운되면 해당 브로커의 리더 파티션은 사용이 불가하므로 팔로워 파티션 중 하나가 리더 파티션의 지위 넘겨 받음

#### 브로커의 역할
- 컨트롤러
  - 클러스터의 다수의 브로커 중 한 대가 컨트롤러 역할
  - 다른 브로커들의 상태를 체크하고 브로커가 클러스터에서 빠지는 경우 해당 브로커에 존재하는 리더 파티션 재분배
  - 컨트롤러 역할을 하는 브로커에 장애가 생기면 다른 브토커가 컨트롤러 역할
- 데이터 삭제
  - 카프카는 컨슈머가 데이터를 가져가더라고 토픽의 데이터가 삭제되지 않음
  - 브로커만이 데이터를 삭제할 수 있음
  - 파일 단위로 데이터가 삭제 됨(log segment)
  - `delete`: 시간, 용량에 따라 삭제
  - `compact`: 각 키의 가장 최신의 레코드를 제외하고 삭제
- 컨슈머 offset 저장
  - __consumer_offsets 토픽에 저장
  - commit: 컨슈머가 데이터를 어디까지 가져왔는지 기록
- 그룹 코디네이터
  - 컨슈머 그룹의 상태를 체크하고 파티션을 컨슈머와 매칭되도록 분배
  - 리밸런스(rebalance): 파티션을 다른 컨슈머로 재할당
- 복제(replication)
  - 클러스터로 묶인 브로커 중 일부에 장애가 발생하더라도 데이터를 유실하지 않고 안전하게 사용하기 위해 데이터 복제
  - 데이터 복제는 파티션 단위로 이루어짐

### 7. Consumer Group
- 하나의 그룹 내 여러 Consumer가 병렬로 파티션을 나눠 읽기 위해 사용

### 8. ISR(In-Sync-Replicas)
- 리더 파티션과 팔로워 파티션이 모두 싱크가 된 상태
- 동기화 완료: 리더 파티션의 모든 데이터가 팔로워 파티션에 복제된 상태

---

## Kafka 실습 환경 (Docker Compose)

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

```bash
# 실행
docker-compose up -d
```

## Java + Spring Boot 연동 예시
```java
// KafkaProducer.java
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("Produced message: {}", message);
    }
}
```

```java
// KafkaConsumer.java
@Slf4j
@Component
public class KafkaConsumer {
    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consume(String message) {
        log.info("Consumed message: {}", message);
    }
}
```

```yaml
# application.yml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: test-group
      auto-offset-reset: earliest
```

