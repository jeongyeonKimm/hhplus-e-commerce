package kr.hhplus.be.server.application.external.dto;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

public class OrderData {

    private Long orderId;
    private Long userId;
    private Long userCouponId;
    private Boolean isCouponApplied;
    private Integer totalAmount;
    private List<OrderProductData> orderProducts;

    @Builder
    private OrderData(Long orderId, Long userId, Long userCouponId, Boolean isCouponApplied, Integer totalAmount, List<OrderProductData> orderProducts) {
        this.orderId = orderId;
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.isCouponApplied = isCouponApplied;
        this.totalAmount = totalAmount;
        this.orderProducts = orderProducts;
    }

    @Builder
    public static OrderData from(Order order, List<OrderProduct> orderProducts) {
        List<OrderProductData> data = new ArrayList<>();
        for (OrderProduct orderProduct : orderProducts) {
            data.add(orderProduct.toData());
        }

        return OrderData.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .userCouponId(order.getUserCouponId())
                .isCouponApplied(order.getIsCouponApplied())
                .totalAmount(order.getTotalAmount())
                .orderProducts(data)
                .build();
    }
}
