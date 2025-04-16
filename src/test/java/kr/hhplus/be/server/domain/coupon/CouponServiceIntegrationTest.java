package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CouponServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @DisplayName("사용자 ID와 쿠폰 ID를 받아 사용자에게 쿠폰을 발급한다.")
    @Test
    void issueCoupon() {
        User user = userRepository.save(User.of());
        long initialStock = 100L;
        Coupon coupon = couponRepository.save(Coupon.of(
                "coupon1",
                1000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                initialStock)
        );

        couponService.issueCoupon(user.getId(), coupon.getId());

        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(user.getId());
        assertThat(userCoupons).hasSize(1);
        assertThat(coupon.getStock()).isEqualTo(initialStock - 1);
    }

    @DisplayName("사용자 쿠폰 ID를 받아 사용자 쿠폰 사용 여부를 롤백한다.")
    @Test
    void rollbackCoupon() {
        User user = userRepository.save(User.of());
        Coupon coupon = couponRepository.save(Coupon.of(
                "coupon1",
                1000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                100L)
        );
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.of(user, coupon));

        couponService.rollbackCoupon(userCoupon.getId());

        assertThat(userCoupon.getIsUsed()).isFalse();
    }
}
