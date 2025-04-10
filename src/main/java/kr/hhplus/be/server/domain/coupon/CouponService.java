package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public boolean redeemCoupon(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(INVALID_USER_COUPON));

        if (!userCoupon.getUserId().equals(userId)) {
            throw new ApiException(COUPON_NOT_OWNED);
        }

        userCoupon.redeem();
        userCouponRepository.save(userCoupon);

        return userCoupon.getIsUsed();
    }

    public int calculateFinalAmount(Long userCouponId, int totalAmount) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_OWNED));

        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new ApiException(INVALID_COUPON));

        return coupon.calculateFinalAmount(totalAmount);
    }
}
