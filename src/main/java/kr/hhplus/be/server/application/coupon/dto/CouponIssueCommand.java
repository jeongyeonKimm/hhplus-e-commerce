package kr.hhplus.be.server.application.coupon.dto;

import lombok.Getter;

@Getter
public class CouponIssueCommand {

    private Long userId;
    private Long couponId;

    public CouponIssueCommand(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }
}
