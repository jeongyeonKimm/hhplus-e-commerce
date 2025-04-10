package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.external.dto.OrderProductData;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
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

    private OrderProduct(Long id, Long orderId, Long productId, Integer amount, Integer quantity) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
        this.quantity = quantity;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static OrderProduct of(Long id, Long orderId, Long productId, Integer amount, Integer quantity) {
        return new OrderProduct(id, orderId, productId, amount, quantity);
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderProductData toData() {
        return OrderProductData.of(productId, amount, getQuantity());
    }
}
