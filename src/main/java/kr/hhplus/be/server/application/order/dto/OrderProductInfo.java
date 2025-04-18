package kr.hhplus.be.server.application.order.dto;

import lombok.Getter;

@Getter
public class OrderProductInfo {

    private Long productId;
    private Long price;
    private Long quantity;

    private OrderProductInfo(Long productId, Long price, Long quantity) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderProductInfo of(Long productId, Long price, Long quantity) {
        return new OrderProductInfo(productId, price, quantity);
    }
}
