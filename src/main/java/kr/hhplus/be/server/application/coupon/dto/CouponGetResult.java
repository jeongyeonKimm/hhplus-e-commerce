package kr.hhplus.be.server.application.coupon.dto;

import kr.hhplus.be.server.domain.coupon.Coupon;
import lombok.Getter;

import java.util.List;

@Getter
public class CouponGetResult {

    private Long userId;
    private List<Coupon> coupons;

    private CouponGetResult(Long userId, List<Coupon> coupons) {
        this.userId = userId;
        this.coupons = coupons;
    }

    public static CouponGetResult from(Long userId, List<Coupon> coupons) {
        return new CouponGetResult(userId, coupons);
    }
}
