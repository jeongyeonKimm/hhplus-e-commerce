package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static kr.hhplus.be.server.common.exception.ErrorCode.INSUFFICIENT_COUPON_STOCK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @DisplayName("정률 할인의 경우 주어진 할인 비율만큼 할인한다.")
    @Test
    void calculateFinalAmount_RATE_success() {
        long totalAmount = 1_000_000L;
        Coupon coupon = Coupon.of(
                1L,
                "회원가입 할인 쿠폰",
                10L,
                DiscountType.RATE,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                100L
        );

        long discountedAmount = coupon.getDiscountAmount(totalAmount);

        long expectedAmount = totalAmount * coupon.getDiscountValue() / 100;
        assertThat(discountedAmount).isEqualTo(expectedAmount);
    }

    @DisplayName("정액 할인의 경우 totalAmount가 할인 값 이하이면 저장된 할인 값을 반환한다.")
    @Test
    void calculateFinalAmount_AMOUNT_whenOriginalAmountIsLessThanOrEqualDiscountValue() {
        long totalAmount = 50000L;
        Coupon coupon = Coupon.of(
                1L,
                "회원가입 할인 쿠폰",
                10000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                100L
        );

        long finalAmount = coupon.getDiscountAmount(totalAmount);

        long expectedAmount = coupon.getDiscountValue();
        assertThat(finalAmount).isEqualTo(expectedAmount);
    }

    @DisplayName("정액 할인의 경우 할인 값이 totalAmount를 초과하면 totalAmount를 반환한다.")
    @Test
    void calculateFinalAmount_AMOUNT_whenOriginalAmountIsMoreThanDiscountValue() {
        long totalAmount = 50000L;
        Coupon coupon = Coupon.of(
                1L,
                "회원가입 할인 쿠폰",
                60000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                100L
        );

        long discountedAmount = coupon.getDiscountAmount(totalAmount);

        assertThat(discountedAmount).isEqualTo(totalAmount);
    }

    @DisplayName("쿠폰 수량이 0 이하인 경우 쿠폰 재고 차감이 불가하다.")
    @Test
    void deduct_throwInsufficientCouponStock_whenInsufficientCouponStock() {
        Coupon coupon = Coupon.of(
                1L,
                "회원가입 할인 쿠폰",
                60000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                0L
        );

        assertThatThrownBy(coupon::deduct)
                .isInstanceOf(ApiException.class)
                .hasMessage(INSUFFICIENT_COUPON_STOCK.getMessage());
    }

    @DisplayName("쿠폰 수량이 0 보다 많은 경우 쿠폰 재고가 차감된다.")
    @Test
    void deduct() {
        long initialStock = 10L;
        Coupon coupon = Coupon.of(
                1L,
                "회원가입 할인 쿠폰",
                60000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                initialStock
        );

        coupon.deduct();

        long expectedStock = initialStock - 1;
        assertThat(coupon.getStock()).isEqualTo(expectedStock);
    }
}
