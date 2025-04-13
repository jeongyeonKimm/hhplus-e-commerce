package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.external.dto.OrderProductData;
import kr.hhplus.be.server.domain.product.Product;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderProduct {

    private Long productId;
    private Long price;
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private OrderProduct(Long productId, Long price, Long quantity) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static OrderProduct of(Long productId, Long price, Long quantity) {
        return new OrderProduct(productId, price, quantity);
    }

    public static OrderProduct of(Product product, Long quantity) {
        return new OrderProduct(
                product.getId(),
                product.getPrice(),
                quantity
        );
    }

    public OrderProductData toData() {
        return OrderProductData.of(productId, price, getQuantity());
    }

    public Long getTotalPrice() {
        return this.price * this.quantity;
    }
}
