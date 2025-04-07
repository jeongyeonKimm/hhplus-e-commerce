package kr.hhplus.be.server.interfaces.api.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProductRequest {

    @Positive
    private Long productId;

    @Min(value = 1)
    private Integer quantity;

    @Builder
    private OrderProductRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
