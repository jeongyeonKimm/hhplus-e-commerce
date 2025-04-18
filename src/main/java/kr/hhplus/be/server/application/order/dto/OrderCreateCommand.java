package kr.hhplus.be.server.application.order.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderCreateCommand {

    private Long userId;
    private Long userCouponId;
    private List<OrderProductInfo> productInfos;

    private OrderCreateCommand(Long userId, Long userCouponId, List<OrderProductInfo> productInfos) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.productInfos = productInfos;
    }

    public static OrderCreateCommand of(Long userId, Long userCouponId, List<OrderProductInfo> productInfos) {
        return new OrderCreateCommand(userId, userCouponId, productInfos);
    }
}
