package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

        boolean alreadyIssued = userCouponRepository.existByUserIdAndCouponId(userId, couponId);
        if (alreadyIssued) {
            throw new ApiException(COUPON_ALREADY_ISSUED);
        }

        coupon.deduct();
        couponRepository.save(coupon);

        UserCoupon userCoupon = UserCoupon.of(generateId(), userId, coupon.getId());
        userCouponRepository.save(userCoupon);
    }

    public List<Coupon> getCoupons(Long userId) {
        return userCouponRepository.findByUserId(userId);
    }

    public UserCoupon getUserCoupon(Long userCouponId) {
        return userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_OWNED));
    }

    public void rollbackCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_OWNED));

        userCoupon.rollback();
        userCouponRepository.save(userCoupon);
    }

    private long generateId() {
        return sequence++;
    }
}
