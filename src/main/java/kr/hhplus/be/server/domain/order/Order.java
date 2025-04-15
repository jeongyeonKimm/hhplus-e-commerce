package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;
import static kr.hhplus.be.server.domain.order.OrderStatus.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
@Entity
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long userCouponId;

    private Boolean isCouponApplied;

    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Transient
    private final List<OrderProduct> orderProducts = new ArrayList<>();

    private Order(Long userId) {
        this.userId = userId;
        this.isCouponApplied = false;
        this.totalAmount = 0L;
        this.status = NOT_PAID;
    }

    public static Order of(Long userId) {
        return new Order(userId);
    }

    public void addProduct(Product product, Long quantity) {
        product.deduct(quantity);

        OrderProduct orderProduct = OrderProduct.of(this, product, quantity);
        this.orderProducts.add(orderProduct);
        this.totalAmount += orderProduct.getTotalPrice();
    }

    public void applyCoupon(UserCoupon userCoupon) {
        if (this.isCouponApplied) {
            throw new ApiException(COUPON_ALREADY_APPLIED);
        }

        if (!userCoupon.isAvailable()) {
            throw new ApiException(INVALID_COUPON);
        }

        this.userCouponId = userCoupon.getId();
        this.totalAmount -= userCoupon.getDiscountAmount(this.totalAmount);
        this.isCouponApplied = true;
        userCoupon.markUsed();
    }

    public void pay() {
        if (this.status != NOT_PAID) {
            throw new ApiException(ORDER_PAYMENT_INVALID_STATE);
        }

        this.status = PAID;
    }

    public void expired(List<Product> products) {
        for (OrderProduct orderProduct : orderProducts) {
            findProduct(products, orderProduct)
                    .ifPresent(orderProduct::restoreStock);
        }

        this.status = EXPIRED;
    }

    public List<Long> getProductIds() {
        return orderProducts.stream()
                .map(OrderProduct::getProductId)
                .toList();
    }

    private Optional<Product> findProduct(List<Product> products, OrderProduct orderProduct) {
        return products.stream()
                .filter(p -> p.getId().equals(orderProduct.getProductId()))
                .findFirst();
    }
}
