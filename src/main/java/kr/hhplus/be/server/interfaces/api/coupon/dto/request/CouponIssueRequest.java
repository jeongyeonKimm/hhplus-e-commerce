package kr.hhplus.be.server.interfaces.api.coupon.dto.request;

import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.coupon.dto.CouponIssueCommand;
import lombok.Getter;

@Getter
public class CouponIssueRequest {

    @Positive
    private Long userId;

    @Positive
    private Long couponId;

    private CouponIssueRequest(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }

    public static CouponIssueRequest of(Long userId, Long couponId) {
        return new CouponIssueRequest(userId, couponId);
    }

    public CouponIssueCommand toCouponIssueCommand() {
        return new CouponIssueCommand(userId, couponId);
    }
}
