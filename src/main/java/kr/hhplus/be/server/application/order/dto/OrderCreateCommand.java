package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCreateCommand {

    private Long userId;
    private Long userCouponId;
    private OrderProductList orderProducts;

    @Builder
    private OrderCreateCommand(Long userId, Long userCouponId, OrderProductList orderProducts) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderProducts = orderProducts;
    }

    public Order toOrder(boolean isCouponApplied, int finalAmount) {
        return Order.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .isCouponApplied(isCouponApplied)
                .totalAmount(finalAmount)
                .build();
    }
}
