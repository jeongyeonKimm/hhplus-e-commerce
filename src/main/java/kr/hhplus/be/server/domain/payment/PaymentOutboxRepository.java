package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.support.event.EventStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentOutboxRepository {

    Optional<PaymentOutbox> findByOrderId(Long orderId);

    List<PaymentOutbox> findAllByEventStatus(EventStatus eventStatus);

    Optional<PaymentOutbox> findByEventId(String eventId);

    void save(PaymentOutbox outbox);
}
