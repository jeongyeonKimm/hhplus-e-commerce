package kr.hhplus.be.server.interfaces.event.coupon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.CouponEvent;
import kr.hhplus.be.server.domain.coupon.CouponOutbox;
import kr.hhplus.be.server.domain.coupon.CouponOutboxService;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static kr.hhplus.be.server.support.event.EventStatus.INIT;

@RequiredArgsConstructor
@Component
public class CouponEventListener {

    private final CouponOutboxService couponOutboxService;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(CouponEvent.Reserved event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);

        CouponOutbox outbox = CouponOutbox.of(event.couponId(), event.id(), event.eventType(), INIT, payload);
        couponOutboxService.save(outbox);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompletedEvent(CouponEvent.Reserved event) {
        kafkaProducer.publish("coupon-reserved", String.valueOf(event.couponId()), event);
    }
}
