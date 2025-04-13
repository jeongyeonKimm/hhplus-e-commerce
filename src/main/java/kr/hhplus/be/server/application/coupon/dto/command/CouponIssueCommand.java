package kr.hhplus.be.server.application.coupon.dto.command;

import lombok.Getter;

@Getter
public class CouponIssueCommand {

    private Long userId;
    private Long couponId;

    private CouponIssueCommand(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }

    public static CouponIssueCommand of(Long userId, Long couponId) {
        return new CouponIssueCommand(userId, couponId);
    }
}
