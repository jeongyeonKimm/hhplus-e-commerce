package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.common.exception.ErrorCode.COUPON_NOT_OWNED;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final UserCouponRepository userCouponRepository;

    public void redeemCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_OWNED));

        userCoupon.redeem();
        userCouponRepository.save(userCoupon);
    }
}
