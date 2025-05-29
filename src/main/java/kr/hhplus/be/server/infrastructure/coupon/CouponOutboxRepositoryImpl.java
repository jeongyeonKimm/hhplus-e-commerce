package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponOutbox;
import kr.hhplus.be.server.domain.coupon.CouponOutboxRepository;
import kr.hhplus.be.server.support.event.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CouponOutboxRepositoryImpl implements CouponOutboxRepository {

    private final CouponOutboxJpaRepository couponOutboxJpaRepository;

    @Override
    public List<CouponOutbox> findAllByEventStatus(EventStatus eventStatus) {
        return couponOutboxJpaRepository.findAllByEventStatus(eventStatus);
    }

    @Override
    public Optional<CouponOutbox> findByEventId(String eventId) {
        return couponOutboxJpaRepository.findByEventId(eventId);
    }

    @Override
    public void save(CouponOutbox outbox) {
        couponOutboxJpaRepository.save(outbox);
    }
}
