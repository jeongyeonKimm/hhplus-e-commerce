package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository {

    UserCoupon save(UserCoupon userCoupon);

    void saveAll(List<UserCoupon> userCoupons);

    Optional<UserCoupon> findById(Long userCouponId);

    Boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    List<UserCoupon> findByUserId(Long userId);

    List<UserCoupon> findByCouponId(Long couponId);
}
