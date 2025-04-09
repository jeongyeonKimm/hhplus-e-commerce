package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.Order;
import lombok.Getter;

@Getter
public class OrderCreateCommand {

    private Long userId;
    private Long userCouponId;
    private OrderProductList orderProducts;

    public Order toOrder(boolean isCouponApplied, int finalAmount) {
        return Order.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .isCouponApplied(isCouponApplied)
                .totalAmount(finalAmount)
                .build();
    }
}
