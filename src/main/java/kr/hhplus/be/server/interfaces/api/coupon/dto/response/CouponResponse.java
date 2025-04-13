package kr.hhplus.be.server.interfaces.api.coupon.dto.response;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.DiscountType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CouponResponse {

    private Long id;
    private String title;
    private DiscountType discountType;
    private Long discountValue;
    private LocalDate startDate;
    private LocalDate endDate;

    private CouponResponse(Long id, String title, DiscountType discountType, Long discountValue, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.title = title;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static CouponResponse of(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getTitle(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getStartDate(),
                coupon.getEndDate()
        );
    }
}
