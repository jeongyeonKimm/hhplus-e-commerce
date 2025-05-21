package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.Order;
import lombok.Getter;

@Getter
public class OrderExpireCommand {

    private Long orderId;

    private Long userId;

    private Long userCouponId;

    private Boolean isCouponApplied;

    private Long totalAmount;

    private OrderExpireCommand(Long orderId, Long userId, Long userCouponId, Boolean isCouponApplied, Long totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.isCouponApplied = isCouponApplied;
        this.totalAmount = totalAmount;
    }

    public static OrderExpireCommand of(Order order) {
        return new OrderExpireCommand(
                order.getId(),
                order.getUserId(),
                order.getUserCouponId(),
                order.getIsCouponApplied(),
                order.getTotalAmount()
        );
    }
}
