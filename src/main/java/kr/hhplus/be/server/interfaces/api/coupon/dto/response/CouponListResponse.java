package kr.hhplus.be.server.interfaces.api.coupon.dto.response;

import kr.hhplus.be.server.application.coupon.dto.CouponGetResult;
import kr.hhplus.be.server.domain.coupon.Coupon;
import lombok.Getter;

import java.util.List;

@Getter
public class CouponListResponse {

    private Long userId;
    private List<CouponResponse> coupons;

    private CouponListResponse(Long userId, List<CouponResponse> coupons) {
        this.userId = userId;
        this.coupons = coupons;
    }

    public static CouponListResponse from(CouponGetResult result) {
        List<Coupon> couponsList = result.getCoupons();
        List<CouponResponse> coupons = couponsList.stream()
                .map(CouponResponse::of)
                .toList();

        return new CouponListResponse(result.getUserId(), coupons);
    }
}
