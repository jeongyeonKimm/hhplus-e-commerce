package kr.hhplus.be.server.api.coupon.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CouponListResponse {

    private Long userId;
    private List<CouponResponse> coupons;

    @Builder
    private CouponListResponse(Long userId, List<CouponResponse> coupons) {
        this.userId = userId;
        this.coupons = coupons;
    }
}
