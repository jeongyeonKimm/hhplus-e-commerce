package kr.hhplus.be.server.infrastructure.payment;

import kr.hhplus.be.server.domain.payment.PaymentOutbox;
import kr.hhplus.be.server.support.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentOutboxSpringRepository extends JpaRepository<PaymentOutbox, Long> {

    Optional<PaymentOutbox> findByOrderId(Long orderId);

    List<PaymentOutbox> findAllByEventStatus(EventStatus eventStatus);

    Optional<PaymentOutbox> findByEventId(String eventId);
}
