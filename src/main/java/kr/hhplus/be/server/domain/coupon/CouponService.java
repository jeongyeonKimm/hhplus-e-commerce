package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.support.aop.lock.RedissonLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @RedissonLock(key = "'coupon:' + #couponId")
    @Transactional
    public void issueCoupon(Long userId, Long couponId) {
        Coupon coupon = couponRepository.findByIdWithLock(couponId)
                .orElseThrow(() -> new ApiException(INVALID_COUPON));

        if (coupon.getStock() <= 0) {
            throw new ApiException(INSUFFICIENT_COUPON_STOCK);
        }

        boolean alreadyIssued = userCouponRepository.existsByUserIdAndCouponId(userId, couponId);
        if (alreadyIssued) {
            throw new ApiException(COUPON_ALREADY_ISSUED);
        }

        coupon.deduct();

        UserCoupon userCoupon = UserCoupon.of(userId, coupon.getId());
        userCouponRepository.save(userCoupon);
    }

    public List<UserCoupon> getCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        List<Long> couponIds = userCoupons.stream()
                .map(UserCoupon::getCouponId)
                .toList();

        Map<Long, Coupon> couponMap = couponRepository.findAllById(couponIds).stream()
                .collect(Collectors.toMap(Coupon::getId, Function.identity()));

        for (UserCoupon userCoupon : userCoupons) {
            userCoupon.setCoupon(couponMap.get(userCoupon.getCouponId()));
        }

        return userCoupons;
    }

    public UserCoupon getUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_OWNED));

        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new ApiException(INVALID_COUPON));

        userCoupon.setCoupon(coupon);
        return userCoupon;
    }

    @Transactional
    public void rollbackCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new ApiException(COUPON_NOT_OWNED));

        userCoupon.rollback();
        userCouponRepository.save(userCoupon);
    }
}
