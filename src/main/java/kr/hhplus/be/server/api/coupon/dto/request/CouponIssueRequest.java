package kr.hhplus.be.server.api.coupon.dto.request;

import lombok.Getter;

@Getter
public class CouponIssueRequest {

    private Long userId;
    private Long couponId;
}
