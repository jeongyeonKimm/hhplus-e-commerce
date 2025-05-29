package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.support.event.DomainEvent;

import java.time.LocalDateTime;

public class CouponEvent {

    public record Reserved(
            Long couponId,
            Long userId,
            LocalDateTime createdAt
    ) implements DomainEvent {

        public static Reserved from(Long couponId, Long userId) {
            return new Reserved(
                    couponId,
                    userId,
                    LocalDateTime.now()
            );
        }

        @Override
        public Long aggregateId() {
            return this.couponId;
        }
    }
}
