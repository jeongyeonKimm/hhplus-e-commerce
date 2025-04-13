package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.INSUFFICIENT_COUPON_STOCK;

@Getter
public class Coupon {

    private Long id;
    private String title;
    private Long discountValue;
    private DiscountType discountType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Coupon(Long id, String title, Long discountValue, DiscountType discountType, LocalDate startDate, LocalDate endDate, Long stock) {
        this.id = id;
        this.title = title;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stock = stock;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Coupon of(Long id, String title, Long discountValue, DiscountType discountType, LocalDate startDate, LocalDate endDate, Long stock) {
        return new Coupon(id, title, discountValue, discountType, startDate, endDate, stock);
    }

    public Long getDiscountAmount(Long totalAmount) {
        if (this.discountType == DiscountType.RATE) {
            return totalAmount * discountValue / 100;
        }

        return Math.min(discountValue, totalAmount);
    }

    public void deduct() {
        if (this.stock <= 0) {
            throw new ApiException(INSUFFICIENT_COUPON_STOCK);
        }

        this.stock -= 1;
    }

    public boolean isExpired() {
        return this.endDate.isBefore(LocalDate.now());
    }
}
