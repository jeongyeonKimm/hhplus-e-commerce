package kr.hhplus.be.server.domain.order;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Order {

    private Long id;
    private Long userId;
    private Long userCouponId;
    private Boolean isCouponApplied;
    private Integer totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Order(Long id, Long userId, Long userCouponId, Boolean isCouponApplied, Integer totalAmount) {
        this.id = id;
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.isCouponApplied = isCouponApplied;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.NOT_PAID;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
