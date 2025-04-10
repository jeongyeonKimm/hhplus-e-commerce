package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static kr.hhplus.be.server.common.exception.ErrorCode.INSUFFICIENT_COUPON_STOCK;
import static org.assertj.core.api.Assertions.*;

class CouponTest {

    @DisplayName("정률 할인의 경우 주어진 할인 비율만큼 할인한다.")
    @Test
    void calculateFinalAmount_RATE_success() {
        int originalAmount = 1_000_000;
        Coupon coupon = Coupon.builder()
                .id(1L)
                .title("회원가입 할인 쿠폰")
                .discountValue(10)
                .discountType(DiscountType.RATE)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .stock(100)
                .build();

        int finalAmount = coupon.calculateFinalAmount(originalAmount);

        int discountAmount = originalAmount * coupon.getDiscountValue() / 100;
        int expectedAmount = originalAmount - discountAmount;
        assertThat(finalAmount).isEqualTo(expectedAmount);
    }

    @DisplayName("정액 할인의 경우 origianlAmount가 할인 값 이하이면 originalAmount에서 할인 금액을 뺀 값을 반환한다.")
    @Test
    void calculateFinalAmount_AMOUNT_whenOriginalAmountIsLessThanOrEqualDiscountValue() {
        int originalAmount = 50_000;
        Coupon coupon = Coupon.builder()
                .id(1L)
                .title("회원가입 할인 쿠폰")
                .discountValue(10_000)
                .discountType(DiscountType.AMOUNT)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .stock(100)
                .build();

        int finalAmount = coupon.calculateFinalAmount(originalAmount);

        int expectedAmount = originalAmount - coupon.getDiscountValue();
        assertThat(finalAmount).isEqualTo(expectedAmount);
    }

    @DisplayName("정액 할인의 경우 할인 값이 origianlAmount를 초과하면 0을 반환한다.")
    @Test
    void calculateFinalAmount_AMOUNT_whenOriginalAmountIsMoreThanDiscountValue() {
        int originalAmount = 50_000;
        Coupon coupon = Coupon.builder()
                .id(1L)
                .title("회원가입 할인 쿠폰")
                .discountValue(60_000)
                .discountType(DiscountType.AMOUNT)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .stock(100)
                .build();

        int finalAmount = coupon.calculateFinalAmount(originalAmount);

        assertThat(finalAmount).isEqualTo(0);
    }

    @DisplayName("쿠폰 수량이 0 이하인 경우 쿠폰 재고 차감이 불가하다.")
    @Test
    void deduct_throwInsufficientCouponStock_whenInsufficientCouponStock() {
        Coupon coupon = Coupon.builder()
                .id(1L)
                .title("회원가입 할인 쿠폰")
                .discountValue(60_000)
                .discountType(DiscountType.AMOUNT)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .stock(0)
                .build();

        assertThatThrownBy(coupon::deduct)
                .isInstanceOf(ApiException.class)
                .hasMessage(INSUFFICIENT_COUPON_STOCK.getMessage());
    }

    @DisplayName("쿠폰 수량이 0 보다 많은 경우 쿠폰 재고가 차감된다.")
    @Test
    void deduct() {
        int initialStock = 10;
        Coupon coupon = Coupon.builder()
                .id(1L)
                .title("회원가입 할인 쿠폰")
                .discountValue(60_000)
                .discountType(DiscountType.AMOUNT)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .stock(initialStock)
                .build();

        coupon.deduct();

        int expectedStock = initialStock - 1;
        assertThat(coupon.getStock()).isEqualTo(expectedStock);
    }
}
