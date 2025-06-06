package kr.hhplus.be.server.interfaces.event.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.payment.PaymentOutbox;
import kr.hhplus.be.server.domain.payment.PaymentOutboxService;
import kr.hhplus.be.server.infrastructure.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static kr.hhplus.be.server.support.event.EventStatus.INIT;

@RequiredArgsConstructor
@Component
public class DataPlatformPaymentEventListener {

    private final PaymentOutboxService paymentOutboxService;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(PaymentEvent.Completed event) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(event);

        PaymentOutbox outbox = PaymentOutbox.of(event.orderId(), event.id(), event.eventType(), INIT, payload);
        paymentOutboxService.save(outbox);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompletedEvent(PaymentEvent.Completed event) {
        kafkaProducer.publish("order-data", event);
    }
}
