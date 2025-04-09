package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class CouponTest {

    @DisplayName("정률 할인의 경우 주어진 할인 비율만큼 할인한다.")
    @Test
    void calculateFinalAmount_RATE_success() {
        int originalAmount = 1_000_000;
        Coupon coupon = Coupon.create(
                1L,
                "회원가입 할인 쿠폰",
                10,
                DiscountType.RATE,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                100
        );

        int finalAmount = coupon.calculateFinalAmount(originalAmount);

        int discountAmount = originalAmount * coupon.getDiscountValue() / 100;
        int expectedAmount = originalAmount - discountAmount;
        assertThat(finalAmount).isEqualTo(expectedAmount);
    }

    @DisplayName("정액 할인의 경우 origianlAmount가 할인 값 이하이면 originalAmount에서 할인 금액을 뺀 값을 반환한다.")
    @Test
    void calculateFinalAmount_AMOUNT_whenOriginalAmountIsLessThanOrEqualDiscountValue() {
        int originalAmount = 50_000;
        Coupon coupon = Coupon.create(
                1L,
                "회원가입 할인 쿠폰",
                10_000,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                100
        );

        int finalAmount = coupon.calculateFinalAmount(originalAmount);

        int expectedAmount = originalAmount - coupon.getDiscountValue();
        assertThat(finalAmount).isEqualTo(expectedAmount);
    }

    @DisplayName("정액 할인의 경우 할인 값이 origianlAmount를 초과하면 0을 반환한다.")
    @Test
    void calculateFinalAmount_AMOUNT_whenOriginalAmountIsMoreThanDiscountValue() {
        int originalAmount = 50_000;
        Coupon coupon = Coupon.create(
                1L,
                "회원가입 할인 쿠폰",
                60_000,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                100
        );

        int finalAmount = coupon.calculateFinalAmount(originalAmount);

        assertThat(finalAmount).isEqualTo(0);
    }
}
