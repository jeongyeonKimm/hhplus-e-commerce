package kr.hhplus.be.server.interfaces.api.order.dto.response;

import kr.hhplus.be.server.application.order.dto.OrderResult;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {

    private Long orderId;

    @Builder
    private OrderResponse(Long orderId) {
        this.orderId = orderId;
    }

    public static OrderResponse from(OrderResult result) {
        return OrderResponse.builder()
                .orderId(result.getOrderId())
                .build();
    }
}
