package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @DisplayName("충전하려는 금액이 0 이하이면 InvalidChargeAmountException이 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1000})
    void charge_shouldThrowInvalidChargeAmountException_whenChargeAmountIsZeroOrNegative(Integer chargeAmount) {
        Point point = Point.builder()
                .id(1L)
                .userId(2L)
                .balance(1000)
                .build();

        assertThatThrownBy(() -> point.charge(chargeAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.INVALID_CHARGE_AMOUNT.getMessage());
    }

    @DisplayName("충전하려는 금액이 1,000,000을 초과하면 InvalidChargeAmountException이 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {1_000_001, 2_000_000})
    void charge_shouldThrowInvalidChargeAmountException_whenChargeAmountIsExceeded(Integer chargeAmount) {
        Point point = Point.builder()
                .id(1L)
                .userId(2L)
                .balance(1000)
                .build();

        assertThatThrownBy(() -> point.charge(chargeAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.INVALID_CHARGE_AMOUNT.getMessage());
    }

    @DisplayName("누적 충전 금액이 5,000,000을 초과하면 ChargeAmountExceedsLimitException이 발생한다.")
    @Test
    @ValueSource()
    void charge_shouldThrowChargeAmountExceedsLimitException_whenChargeAmountExceedsLimit() {
        int initialAmount = 4_500_000;
        int chargeAmount = 600_000;

        Point point = Point.builder()
                .id(1L)
                .userId(2L)
                .balance(initialAmount)
                .build();

        assertThatThrownBy(() -> point.charge(chargeAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.CHARGE_AMOUNT_EXCEEDS_LIMIT.getMessage());
    }

    @DisplayName("1 ~ 1,000,000 사이의 충전 금액으로 충전 요청을 했을 때 누적 포인트가 5,000,000을 넘지 않으면 포인트 충전에 성공한다.")
    @Test
    void charge_success() {
        int initialAmount = 3_000_000;
        int chargeAmount = 500_000;

        Point point = Point.builder()
                .id(1L)
                .userId(2L)
                .balance(initialAmount)
                .build();

        point.charge(chargeAmount);

        int expectedAmount = initialAmount + chargeAmount;
        assertThat(point.getBalance()).isEqualTo(expectedAmount);
    }

}
