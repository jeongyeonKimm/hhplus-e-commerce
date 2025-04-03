package kr.hhplus.be.server.api.coupon.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CouponResponse {

    private Long id;
    private String title;
    private String discountType;
    private Integer discountValue;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    private CouponResponse(Long id, String title, String discountType, Integer discountValue, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.title = title;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
