package kr.hhplus.be.server.domain.coupon;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Coupon {

    private Long id;
    private String title;
    private Long discountValue;
    private DiscountType discountType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Coupon(Long id, String title, Long discountValue, DiscountType discountType, LocalDate startDate, LocalDate endDate, Integer stock) {
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
}
