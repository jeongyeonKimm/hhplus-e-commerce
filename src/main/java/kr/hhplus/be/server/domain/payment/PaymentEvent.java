package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.support.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PaymentEvent {

    public record Completed(

            String id,
            Long orderId,
            Long userId,
            Long userCouponId,
            Boolean isCouponApplied,
            Long totalAmount,
            List<OrderProduct> orderProducts,
            LocalDateTime payCompletedAt,
            LocalDateTime createdAt
    ) implements DomainEvent {
        public static Completed from(Order order, List<OrderProduct> orderProducts) {
            return new Completed(
                    generateId(),
                    order.getId(),
                    order.getUserId(),
                    order.getUserCouponId(),
                    order.getIsCouponApplied(),
                    order.getTotalAmount(),
                    orderProducts,
                    order.getCreatedAt(),
                    LocalDateTime.now()
            );
        }

        @Override
        public Long aggregateId() {
            return this.orderId;
        }
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
