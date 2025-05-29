package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.support.event.EventStatus;

import java.util.List;
import java.util.Optional;

public interface CouponOutboxRepository {

    Optional<CouponOutbox> findByCouponId(Long couponId);

    List<CouponOutbox> findAllByEventStatus(EventStatus eventStatus);

    Optional<CouponOutbox> findByEventId(String eventId);

    void save(CouponOutbox outbox);
}
