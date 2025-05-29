package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponOutbox;
import kr.hhplus.be.server.support.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponOutboxJpaRepository extends JpaRepository<CouponOutbox, Long> {

    Optional<CouponOutbox> findByCouponId(Long couponId);

    List<CouponOutbox> findAllByEventStatus(EventStatus eventStatus);

    Optional<CouponOutbox> findByEventId(String eventId);
}
