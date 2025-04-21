package kr.hhplus.be.server.application.external.dto;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderData {

    private Long orderId;
    private Long userId;
    private Long userCouponId;
    private Boolean isCouponApplied;
    private Long totalAmount;
    private List<OrderProductData> orderProducts;

    private OrderData(Long orderId, Long userId, Long userCouponId, Boolean isCouponApplied, Long totalAmount, List<OrderProductData> orderProducts) {
        this.orderId = orderId;
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.isCouponApplied = isCouponApplied;
        this.totalAmount = totalAmount;
        this.orderProducts = orderProducts;
    }

    public static OrderData of(Long orderId, Long userId, Long userCouponId, Boolean isCouponApplied, Long totalAmount, List<OrderProductData> orderProducts) {
        return new OrderData(orderId, userId, userCouponId, isCouponApplied, totalAmount, orderProducts);
    }

    public static OrderData from(Order order, List<OrderProduct> orderProducts) {
        List<OrderProductData> data = new ArrayList<>();
        for (OrderProduct orderProduct : orderProducts) {
            data.add(orderProduct.toData());
        }

        return OrderData.of(
                order.getId(),
                order.getUserId(),
                order.getUserCouponId(),
                order.getIsCouponApplied(),
                order.getTotalAmount(),
                data
        );
    }
}
