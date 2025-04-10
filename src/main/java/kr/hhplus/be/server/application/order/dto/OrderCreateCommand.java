package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.Order;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCreateCommand {

    private Long userId;
    private Long userCouponId;
    private OrderProductList orderProducts;

    private OrderCreateCommand(Long userId, Long userCouponId, OrderProductList orderProducts) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderProducts = orderProducts;
    }

    public static OrderCreateCommand of(Long userId, Long userCouponId, OrderProductList orderProducts) {
        return new OrderCreateCommand(userId, userCouponId, orderProducts);
    }

    public Order toOrder(Long id, boolean isCouponApplied, int finalAmount) {
        return Order.of(id, userId, userCouponId, isCouponApplied, finalAmount);
    }
}
