package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static kr.hhplus.be.server.common.exception.ErrorCode.ALREADY_USED_COUPON;
import static kr.hhplus.be.server.common.exception.ErrorCode.COUPON_DATE_EXPIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserCouponTest {

    @DisplayName("아직 사용되지 않았고, 사용 가능 기한에 속하면 쿠폰 적용이 가능하다.")
    @Test
    void redeem_success() {
        UserCoupon userCoupon = UserCoupon.builder()
                .id(1L)
                .userId(2L)
                .couponId(3L)
                .isUsed(false)
                .couponTitle("10% 할인 쿠폰")
                .issuedAt(LocalDate.of(2025, 4, 1))
                .expiredAt(LocalDate.of(2025, 4, 30))
                .build();

        userCoupon.redeem();

        assertThat(userCoupon.getIsUsed()).isTrue();
    }

    @DisplayName("이미 사용된 쿠폰은 쿠폰 적용에 실패하고 AlreadyUsedCouponException이 발생한다.")
    @Test
    void redeem_throwAlreadyUsedCouponException_whenCouponIsAlreadyUsed() {
        UserCoupon userCoupon = UserCoupon.builder()
                .id(1L)
                .userId(2L)
                .couponId(3L)
                .isUsed(true)
                .couponTitle("10% 할인 쿠폰")
                .issuedAt(LocalDate.of(2025, 4, 1))
                .expiredAt(LocalDate.of(2025, 4, 30))
                .build();

        assertThatThrownBy(userCoupon::redeem)
                .isInstanceOf(ApiException.class)
                .hasMessage(ALREADY_USED_COUPON.getMessage());
    }

    @DisplayName("만료된 쿠폰을 사용하면 쿠폰 적용에 실패하고 CouponDateExpiredException이 발생한다.")
    @Test
    void redeem_throwCouponDateExpiredException_whenCouponIsExpired() {
        LocalDate now = LocalDate.now();
        UserCoupon userCoupon = UserCoupon.builder()
                .id(1L)
                .userId(2L)
                .couponId(3L)
                .isUsed(false)
                .couponTitle("10% 할인 쿠폰")
                .issuedAt(now.minusMonths(2))
                .expiredAt(now.minusMonths(1))
                .build();

        assertThatThrownBy(userCoupon::redeem)
                .isInstanceOf(ApiException.class)
                .hasMessage(COUPON_DATE_EXPIRED.getMessage());
    }
}
