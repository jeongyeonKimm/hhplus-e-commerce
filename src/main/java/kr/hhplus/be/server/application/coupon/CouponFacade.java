package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.application.coupon.dto.command.CouponGetCommand;
import kr.hhplus.be.server.application.coupon.dto.CouponGetResult;
import kr.hhplus.be.server.application.coupon.dto.command.CouponIssueCommand;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CouponFacade {

    private final CouponService couponService;

    public void issueCoupon(CouponIssueCommand command) {
        couponService.issueCoupon(command.getUserId(), command.getCouponId());
    }

    public CouponGetResult getCoupons(CouponGetCommand command) {
        List<Coupon> coupons = couponService.getCoupons(command.getUserId());
        return CouponGetResult.from(command.getUserId(), coupons);
    }
}
