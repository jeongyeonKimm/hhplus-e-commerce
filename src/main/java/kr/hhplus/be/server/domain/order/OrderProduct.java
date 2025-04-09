package kr.hhplus.be.server.domain.order;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderProduct {

    private Long id;
    private Long orderId;
    private Long productId;
    private Integer amount;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private OrderProduct(Long id, Long orderId, Long productId, Integer amount, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
