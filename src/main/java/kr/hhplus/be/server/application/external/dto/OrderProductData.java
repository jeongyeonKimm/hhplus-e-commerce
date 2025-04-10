package kr.hhplus.be.server.application.external.dto;

import lombok.Builder;

public class OrderProductData {

    private Long productId;
    private Integer amount;
    private Integer quantity;

    @Builder
    private OrderProductData(Long productId, Integer amount, Integer quantity) {
        this.productId = productId;
        this.amount = amount;
        this.quantity = quantity;
    }
}
