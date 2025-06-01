package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_product")
@Entity
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long productId;

    private Long price;

    private Long quantity;

    private OrderProduct(Long productId, Long orderId, Long price, Long quantity) {
        this.productId = productId;
        this.orderId = orderId;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderProduct of(Long productId, Long orderId, Long price, Long quantity) {
        return new OrderProduct(productId, orderId, price, quantity);
    }

    public static OrderProduct of(Order order, Product product, Long quantity) {
        return new OrderProduct(
                product.getId(),
                order.getId(),
                product.getPrice(),
                quantity
        );
    }

    public Long getTotalPrice() {
        return this.price * this.quantity;
    }

    public void restoreStock(Product product) {
        product.restore(this.quantity);
    }
}
