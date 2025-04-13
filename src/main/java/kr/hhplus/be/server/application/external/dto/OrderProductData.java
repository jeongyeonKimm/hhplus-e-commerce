package kr.hhplus.be.server.application.external.dto;

public class OrderProductData {

    private Long productId;
    private Integer amount;
    private Integer quantity;

    private OrderProductData(Long productId, Integer amount, Integer quantity) {
        this.productId = productId;
        this.amount = amount;
        this.quantity = quantity;
    }

    public static OrderProductData of(Long productId, Integer amount, Integer quantity) {
        return new OrderProductData(productId, amount, quantity);
    }
}
