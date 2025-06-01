package kr.hhplus.be.server.domain.outbox;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducer;
import kr.hhplus.be.server.support.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_OUTBOX;
import static kr.hhplus.be.server.support.event.EventStatus.INIT;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final KafkaProducer kafkaProducer;

    public Outbox getOutbox(DomainEvent event) {
        return outboxRepository.findByAggregateId(event.aggregateId())
                .orElseThrow(() -> new ApiException(INVALID_OUTBOX));
    }

    public void republishOutboxEvent() {
        outboxRepository.findByEventStatus(INIT)
                .forEach(outbox -> {
                    try {
                        kafkaProducer.publish("order-data", outbox.getPayload());
                        outbox.markAsSuccess();
                        log.info("Kafka 메시지 발행 성공: {}", outbox.getPayload());
                    } catch (Exception e)  {
                        outbox.markAsFail();
                        log.error("Kafka 메시지 발행 실패: {}", outbox.getPayload());
                    }
                });
    }
}
