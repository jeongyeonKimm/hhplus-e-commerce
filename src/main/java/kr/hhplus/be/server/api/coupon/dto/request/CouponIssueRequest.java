package kr.hhplus.be.server.api.coupon.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class CouponIssueRequest {

    @Positive
    private Long userId;

    @Positive
    private Long couponId;
}
