package kr.hhplus.be.server.api.coupon.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CouponIssueRequest {

    @Positive
    private Long userId;

    @Positive
    private Long couponId;

    @Builder
    private CouponIssueRequest(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }
}
