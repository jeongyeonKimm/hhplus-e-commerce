package kr.hhplus.be.server.api.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public class OrderProductRequest {

    @Positive
    private Long productId;

    @Min(value = 1)
    private Integer quantity;
}
