package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.payment.PaymentOutbox;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static kr.hhplus.be.server.support.event.EventStatus.SEND_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class CouponServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponOutboxRepository couponOutboxRepository;

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

        Coupon foundCoupon = couponRepository.findById(coupon.getId()).get();
        assertThat(foundCoupon.getStock()).isEqualTo(initialStock - 1);
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

    @DisplayName("쿠폰 발급 요청이 들어오면 중복 발급 여부를 확인하고 쿠폰 예약 이벤트를 발행하고, 아웃박스에 저장한다.")
    @Test
    void requestCouponIssuance() {
        User user = userRepository.save(Instancio.of(User.class)
                .ignore(field(User::getId))
                .create());

        LocalDate today = LocalDate.now();
        Coupon coupon = couponRepository.save(Instancio.of(Coupon.class)
                .ignore(field(Coupon::getId))
                .set(field(Coupon::getStartDate), today)
                .set(field(Coupon::getEndDate), today.plusYears(1))
                .create());

        couponService.requestCouponIssuance(user.getId(), coupon.getId());

        CouponOutbox outbox = couponOutboxRepository.findByCouponId(coupon.getId()).orElseThrow();
        assertThat(outbox).isNotNull();
        assertThat(outbox.getCouponId()).isEqualTo(coupon.getId());
        assertThat(outbox.getEventType()).isEqualTo("CouponEvent.Reserved");

        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    CouponOutbox updatedOutbox = couponOutboxRepository.findByCouponId(coupon.getId()).orElseThrow();
                    assertThat(updatedOutbox.getEventStatus()).isEqualTo(SEND_SUCCESS);
                });
    }
}
