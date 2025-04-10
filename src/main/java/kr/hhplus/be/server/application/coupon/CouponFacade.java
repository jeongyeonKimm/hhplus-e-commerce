package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CouponFacade {

    private final CouponService couponService;

    public void issueCoupon(CouponIssueCommand command) {
        couponService.issueCoupon(command.getUserId(), command.getCouponId());
    }
}
