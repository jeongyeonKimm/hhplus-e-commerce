package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CouponIssueProcessorTest extends IntegrationTestSupport {

    private static final String ISSUED_COUPON_KEY = "coupon:issued:%d";

    @Autowired
    private CouponIssueProcessor couponIssueProcessor;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DisplayName("쿠폰의 재고가 충분한 경우 발급 요청이 모두 처리된다.")
    @Test
    void processCouponIssuance() {
        LocalDate now = LocalDate.now();
        Coupon coupon = Coupon.of(
                "coupon",
                1000L,
                DiscountType.AMOUNT,
                now,
                now.plusYears(1),
                10L);
        Coupon savedCoupon = couponRepository.save(coupon);

        for (int i = 0; i < 10; i++) {
            User user = userRepository.save(User.of());
            couponRepository.requestIssuance(user.getId(), savedCoupon.getId());
        }

        couponIssueProcessor.processCouponIssuance();

        Coupon issuedCoupon = couponRepository.findById(savedCoupon.getId()).orElseThrow();
        assertThat(issuedCoupon.getStock()).isZero();

        List<UserCoupon> userCoupons = userCouponRepository.findByCouponId(coupon.getId());
        assertThat(userCoupons).hasSize(10);
    }

    @DisplayName("쿠폰의 재고가 충분하지 않은 경우 발급 요청만큼만 처리된다.")
    @Test
    void processCouponIssuance_insufficientStock() {
        LocalDate now = LocalDate.now();
        Coupon coupon = Coupon.of(
                "coupon",
                1000L,
                DiscountType.AMOUNT,
                now,
                now.plusYears(1),
                5L);
        Coupon savedCoupon = couponRepository.save(coupon);

        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = userRepository.save(User.of());
            userIds.add(user.getId());
            couponRepository.requestIssuance(user.getId(), savedCoupon.getId());
        }

        couponIssueProcessor.processCouponIssuance();

        Coupon issuedCoupon = couponRepository.findById(savedCoupon.getId()).orElseThrow();
        assertThat(issuedCoupon.getStock()).isZero();

        String issuedKey = String.format(ISSUED_COUPON_KEY, savedCoupon.getId());
        Set<String> successMembers = redisTemplate.opsForSet().members(issuedKey);
        assertThat(successMembers.size()).isEqualTo(5);

        List<UserCoupon> userCoupons = userCouponRepository.findByCouponId(coupon.getId());
        assertThat(userCoupons).hasSize(5);
    }
}
