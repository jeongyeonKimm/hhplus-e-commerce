package kr.hhplus.be.server.application.external.dto;

public class OrderProductData {

    private Long productId;
    private Long amount;
    private Long quantity;

    private OrderProductData(Long productId, Long amount, Long quantity) {
        this.productId = productId;
        this.amount = amount;
        this.quantity = quantity;
    }

    public static OrderProductData of(Long productId, Long amount, Long quantity) {
        return new OrderProductData(productId, amount, quantity);
    }
}
