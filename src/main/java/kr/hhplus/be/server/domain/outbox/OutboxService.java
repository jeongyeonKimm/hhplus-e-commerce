package kr.hhplus.be.server.domain.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.coupon.CouponEvent;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducer;
import kr.hhplus.be.server.support.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_OUTBOX;
import static kr.hhplus.be.server.common.exception.ErrorCode.PARSING_ERROR;
import static kr.hhplus.be.server.support.event.EventStatus.INIT;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    public Outbox getOutbox(DomainEvent event) {
        return outboxRepository.findByAggregateId(event.aggregateId())
                .orElseThrow(() -> new ApiException(INVALID_OUTBOX));
    }

    public void republishOutboxEvent(String topic, boolean useKey) {
        outboxRepository.findByEventStatus(INIT).stream()
                .filter(outbox -> topic.equals(outbox.getTopic()))
                .forEach(outbox -> {
                    try {
                        if (useKey) {
                            kafkaProducer.publish(topic, String.valueOf(outbox.getAggregateId()), outbox.getPayload());

                        } else {
                            kafkaProducer.publish(topic, outbox.getPayload());
                        }
                        outbox.markAsSuccess();
                        log.info("Kafka 메시지 발행 성공: {}", outbox.getPayload());
                    } catch (Exception e)  {
                        outbox.markAsFail();
                        log.error("Kafka 메시지 발행 실패: {}", outbox.getPayload());
                    }
                });
    }

    public List<Outbox> getAllOutboxesByReservedEvent(List<CouponEvent.Reserved> events) {
        return events.stream()
                .map(event -> {
                    try {
                        String payload = objectMapper.writeValueAsString(event);
                        return outboxRepository.findByPayload(payload)
                                .orElseThrow(() -> new ApiException(INVALID_OUTBOX));
                    } catch (JsonProcessingException e) {
                        throw new ApiException(PARSING_ERROR);
                    }
                })
                .toList();
    }
}
