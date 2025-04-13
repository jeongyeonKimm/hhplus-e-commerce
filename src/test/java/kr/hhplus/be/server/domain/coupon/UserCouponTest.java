package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static kr.hhplus.be.server.common.exception.ErrorCode.ALREADY_USED_COUPON;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_COUPON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

class UserCouponTest {

    @DisplayName("이미 사용된 쿠폰의 할인 금액을 계산하려고 하면 InvalidCouponException을 반환한다.")
    @Test
    void getDiscountAmount_throwInvalidCoupon_whenCouponIsAlreadyUsed() {
        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field("isUsed"), true)
                .create();

        assertThatThrownBy(() -> userCoupon.getDiscountAmount(10000L))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_COUPON.getMessage());
    }

    @DisplayName("만료된 쿠폰의 할인 금액을 계산하려고 하면 InvalidCouponException을 반환한다.")
    @Test
    void getDiscountAmount_throwInvalidCoupon_whenCouponIsExpired() {
        LocalDate now = LocalDate.now();
        Coupon coupon = Instancio.of(Coupon.class)
                .set(field("endDate"), now.minusMonths(1))
                .create();

        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field("coupon"), coupon)
                .create();

        assertThatThrownBy(() -> userCoupon.getDiscountAmount(10000L))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_COUPON.getMessage());
    }

    @DisplayName("쿠폰이 사용되지 않았고, 아직 만료 기한이 지나지 않았으면 할인 금액 계산이 정상적으로 이루어진다.")
    @Test
    void getDiscountAmount_success() {
        LocalDate now = LocalDate.now();
        long discountValue = 1000L;
        Coupon coupon = Instancio.of(Coupon.class)
                .set(field("discountType"), DiscountType.AMOUNT)
                .set(field("discountValue"), discountValue)
                .set(field("endDate"), now.plusMonths(1))
                .create();

        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field(UserCoupon::getIsUsed), false)
                .set(field(UserCoupon::getCoupon), coupon)
                .create();

        long discountAmount = userCoupon.getDiscountAmount(10000L);

        assertThat(discountAmount).isEqualTo(discountValue);
    }

    @DisplayName("이미 사용된 상태인 사용자 쿠폰은 쿠폰 사용 여부를 true로 처리하지 못하고 AlreadyUsedException이 발생한다.")
    @Test
    void markUsed_throwAlreadyUsedCoupon_whenUserCouponIsAlreadyUsed() {
        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field("isUsed"), true)
                .create();

        assertThatThrownBy(userCoupon::markUsed)
                .isInstanceOf(ApiException.class)
                .hasMessage(ALREADY_USED_COUPON.getMessage());
    }

    @DisplayName("사용되지 않은 사용자 쿠폰의 사용 여부를 바꾸려하면 사용 여부를 true로 처리한다.")
    @Test
    void markUsed() {
        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field("isUsed"), false)
                .create();

        userCoupon.markUsed();

        assertThat(userCoupon.getIsUsed()).isTrue();
    }
}
