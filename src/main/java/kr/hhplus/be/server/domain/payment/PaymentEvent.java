package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.application.external.dto.OrderData;
import kr.hhplus.be.server.application.external.dto.OrderProductData;

import java.util.List;

public class PaymentEvent {

    public record Completed(
            Long orderId,
            Long userId,
            Long userCouponId,
            Boolean isCouponApplied,
            Long totalAmount,
            List<OrderProductData> orderProducts
    ) {
        public static Completed from(OrderData orderData) {
            return new Completed(
                    orderData.getOrderId(),
                    orderData.getUserId(),
                    orderData.getUserCouponId(),
                    orderData.getIsCouponApplied(),
                    orderData.getTotalAmount(),
                    orderData.getOrderProducts()
            );
        }
    }
}
