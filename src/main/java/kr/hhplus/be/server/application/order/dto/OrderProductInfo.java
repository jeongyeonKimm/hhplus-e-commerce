package kr.hhplus.be.server.application.order.dto;

import kr.hhplus.be.server.domain.order.OrderProduct;
import lombok.Getter;

@Getter
public class OrderProductInfo {

    private Long productId;
    private Integer amount;
    private Integer quantity;

    private OrderProductInfo(Long productId, Integer amount, Integer quantity) {
        this.productId = productId;
        this.amount = amount;
        this.quantity = quantity;
    }

    public static OrderProductInfo of(Long productId, Integer amount, Integer quantity) {
        return new OrderProductInfo(productId, amount, quantity);
    }

    public OrderProduct toOrderProduct() {
        return OrderProduct.of(null, null, productId, amount, quantity);
    }
}
