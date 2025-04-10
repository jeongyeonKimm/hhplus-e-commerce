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
    private long sequence = 1L;

    public void issueCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ApiException(INVALID_COUPON));

        if (coupon.getStock() <= 0) {
            throw new ApiException(INSUFFICIENT_COUPON_STOCK);
        }

        Boolean alreadyIssued = userCouponRepository.existByUserIdAndCouponId(userId, couponId);
        if (alreadyIssued) {
            throw new ApiException(COUPON_ALREADY_ISSUED);
        }

        coupon.deduct();
        couponRepository.save(coupon);

        UserCoupon userCoupon = UserCoupon.issue(generateId(), userId, coupon);
        userCouponRepository.save(userCoupon);
    }

    public boolean redeemCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_OWNED));

        userCoupon.redeem();
        userCouponRepository.save(userCoupon);
        return true;
    }

    public int calculateFinalAmount(Long userCouponId, int totalAmount) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_OWNED));

        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new ApiException(INVALID_COUPON));

        return coupon.calculateFinalAmount(totalAmount);
    }

    private long generateId() {
        return sequence++;
    }
}
