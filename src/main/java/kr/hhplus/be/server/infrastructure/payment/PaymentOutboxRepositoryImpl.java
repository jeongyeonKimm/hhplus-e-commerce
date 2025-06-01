package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.PaymentOutbox;
import kr.hhplus.be.server.domain.payment.PaymentOutboxRepository;
import kr.hhplus.be.server.support.event.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxSpringRepository paymentOutboxSpringRepository;

    @Override
    public Optional<PaymentOutbox> findByOrderId(Long orderId) {
        return paymentOutboxSpringRepository.findByOrderId(orderId);
    }

    @Override
    public List<PaymentOutbox> findAllByEventStatus(EventStatus eventStatus) {
        return paymentOutboxSpringRepository.findAllByEventStatus(eventStatus);
    }

    @Override
    public Optional<PaymentOutbox> findByEventId(String eventId) {
        return paymentOutboxSpringRepository.findByEventId(eventId);
    }

    @Override
    public void save(PaymentOutbox outbox) {
        paymentOutboxSpringRepository.save(outbox);
    }
}
