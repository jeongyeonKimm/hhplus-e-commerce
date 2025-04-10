package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.Order;
import lombok.Getter;

@Getter
public class OrderResult {

    private Long orderId;

    public OrderResult(Long orderId) {
        this.orderId = orderId;
    }

    public static OrderResult from(Order order) {
        return new OrderResult(order.getId());
    }
}
