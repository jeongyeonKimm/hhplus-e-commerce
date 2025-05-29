package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.support.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class CouponEvent {

    public record Reserved(

            String id,
            Long couponId,
            Long userId,
            LocalDateTime createdAt
    ) implements DomainEvent {

        public static Reserved from(Long couponId, Long userId) {
            return new Reserved(
                    generateId(),
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

    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
