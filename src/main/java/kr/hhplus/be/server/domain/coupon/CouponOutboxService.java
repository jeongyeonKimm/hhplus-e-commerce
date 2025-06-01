package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_OUTBOX;
import static kr.hhplus.be.server.support.event.EventStatus.INIT;

@Slf4j
@RequiredArgsConstructor
@Service
public class CouponOutboxService {

    private final CouponOutboxRepository couponOutboxRepository;
    private final KafkaProducer kafkaProducer;

    public void republishCouponOutbox() {
        couponOutboxRepository.findAllByEventStatus(INIT)
                .forEach(outbox -> {
                    try {
                        kafkaProducer.publish("coupon-reserved", String.valueOf(outbox.getCouponId()), outbox.getPayload());
                        outbox.markAsSuccess();
                        log.info("Kafka 메시지 발행 성공: {}", outbox.getPayload());
                    } catch (Exception e)  {
                        outbox.markAsFail();
                        log.error("Kafka 메시지 발행 실패: {}", outbox.getPayload());
                    }
                });
    }

    public List<CouponOutbox> getAllByEvent(List<CouponEvent.Reserved> events) {
        return events.stream()
                .map(event -> couponOutboxRepository.findByEventId(event.id())
                            .orElseThrow(() -> new ApiException(INVALID_OUTBOX)))
                .toList();
    }

    public void save(CouponOutbox outbox) {
        couponOutboxRepository.save(outbox);
    }
}
