package kr.hhplus.be.server.interfaces.api.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
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

    public OrderProductInfo toOrderProductInfo() {
        return OrderProductInfo.of(productId, null, quantity);
    }
}
