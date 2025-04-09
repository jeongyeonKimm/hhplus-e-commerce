package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_COUPON;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final UserCouponRepository userCouponRepository;

    public void redeemCoupon(Long couponId) {
        UserCoupon userCoupon = userCouponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new ApiException(INVALID_COUPON));

        userCoupon.redeem();
        userCouponRepository.save(userCoupon);
    }
}
