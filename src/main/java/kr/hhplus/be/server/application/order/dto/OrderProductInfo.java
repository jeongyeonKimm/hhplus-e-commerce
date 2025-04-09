package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProductInfo {

    private Long productId;
    private Integer amount;
    private Integer quantity;

    @Builder
    private OrderProductInfo(Long productId, Integer amount, Integer quantity) {
        this.productId = productId;
        this.amount = amount;
        this.quantity = quantity;
    }

    public OrderProduct toOrderProduct() {
        return OrderProduct.builder()
                .productId(productId)
                .amount(amount)
                .quantity(quantity)
                .build();
    }
}
