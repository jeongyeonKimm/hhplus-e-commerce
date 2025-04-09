package kr.hhplus.be.server.domain.coupon;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Coupon {

    private Long id;
    private String title;
    private Integer discountValue;
    private DiscountType discountType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Coupon(Long id, String title, Integer discountValue, DiscountType discountType, LocalDate startDate, LocalDate endDate, Integer stock) {
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

    public static Coupon create(Long id, String title, Integer discountValue, DiscountType discountType, LocalDate startDate, LocalDate endDate, Integer stock) {
        return Coupon.builder()
                .id(id)
                .title(title)
                .discountValue(discountValue)
                .discountType(discountType)
                .startDate(startDate)
                .endDate(endDate)
                .stock(stock)
                .build();
    }

    public int calculateFinalAmount(int originalAmount) {
        int discountAmount = calculateDiscountAmount(originalAmount);
        return Math.max(0, originalAmount - discountAmount);
    }

    public int calculateDiscountAmount(int originalAmount) {
        if (this.discountType == DiscountType.RATE) {
            return originalAmount * discountValue / 100;
        }

        return discountValue;
    }
}
