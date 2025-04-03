package kr.hhplus.be.server.api.order.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {

    private Long orderId;

    @Builder
    private OrderResponse(Long orderId) {
        this.orderId = orderId;
    }
}
