package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static kr.hhplus.be.server.common.exception.ErrorCode.INSUFFICIENT_COUPON_STOCK;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "coupon")
@Entity
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Long discountValue;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long stock;

    private Coupon(String title, Long discountValue, DiscountType discountType, LocalDate startDate, LocalDate endDate, Long stock) {
        this.title = title;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.stock = stock;
    }

    public static Coupon of(String title, Long discountValue, DiscountType discountType, LocalDate startDate, LocalDate endDate, Long stock) {
        return new Coupon(title, discountValue, discountType, startDate, endDate, stock);
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
