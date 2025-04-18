package kr.hhplus.be.server.application.coupon.dto;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import lombok.Getter;

import java.util.List;

@Getter
public class CouponGetResult {

    private Long userId;
    private List<UserCoupon> userCoupons;

    private CouponGetResult(Long userId, List<UserCoupon> userCoupons) {
        this.userId = userId;
        this.userCoupons = userCoupons;
    }

    public static CouponGetResult from(Long userId, List<UserCoupon> userCoupons) {
        return new CouponGetResult(userId, userCoupons);
    }
}
