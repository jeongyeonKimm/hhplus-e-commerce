package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PointTest {

    @DisplayName("충전하려는 금액이 0 이하이면 InvalidChargeAmountException이 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {0, -1000})
    void charge_shouldThrowInvalidChargeAmountException_whenChargeAmountIsZeroOrNegative(Long chargeAmount) {
        Point point = Point.of(2L, 1000L);

        assertThatThrownBy(() -> point.charge(chargeAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_CHARGE_AMOUNT.getMessage());
    }

    @DisplayName("충전하려는 금액이 1,000,000을 초과하면 InvalidChargeAmountException이 발생한다.")
    @ParameterizedTest
    @ValueSource(longs = {1_000_001, 2_000_000})
    void charge_shouldThrowInvalidChargeAmountException_whenChargeAmountIsExceeded(Long chargeAmount) {
        Point point = Point.of(2L, 1000L);

        assertThatThrownBy(() -> point.charge(chargeAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_CHARGE_AMOUNT.getMessage());
    }

    @DisplayName("누적 충전 금액이 5,000,000을 초과하면 ChargeAmountExceedsLimitException이 발생한다.")
    @Test
    @ValueSource()
    void charge_shouldThrowChargeAmountExceedsLimitException_whenChargeAmountExceedsLimit() {
        long initialAmount = 4_500_000L;
        long chargeAmount = 600_000L;

        Point point = Point.of(2L, initialAmount);

        assertThatThrownBy(() -> point.charge(chargeAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(CHARGE_AMOUNT_EXCEEDS_LIMIT.getMessage());
    }

    @DisplayName("1 ~ 1,000,000 사이의 충전 금액으로 충전 요청을 했을 때 누적 포인트가 5,000,000을 넘지 않으면 포인트 충전에 성공한다.")
    @Test
    void charge_success() {
        long initialAmount = 3_000_000L;
        long chargeAmount = 500_000L;

        Point point = Point.of(2L, initialAmount);

        point.charge(chargeAmount);

        long expectedAmount = initialAmount + chargeAmount;
        assertThat(point.getBalance()).isEqualTo(expectedAmount);
    }

    @DisplayName("사용하려는 금액이 잔액보다 많으면 포인트 사용에 실패한다.")
    @Test
    void use_shouldThrowInvalidUseAmountException_whenUseAmountIsMoreThanBalance() {
        Point point = Point.of(2L, 1000L);

        assertThatThrownBy(() -> point.use(2000L))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_USE_AMOUNT.getMessage());
    }

    @DisplayName("사용하려는 금액이 0 이하이면 포인트 사용에 실패한다.")
    @ValueSource(longs = {0, -1000})
    @ParameterizedTest
    void use_shouldThrowInvalidUseAmountException_whenUseAmountIsZeroOrNegative(Long useAmount) {
        Point point = Point.of(2L, 1000L);

        assertThatThrownBy(() -> point.use(useAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_USE_AMOUNT.getMessage());
    }

    @DisplayName("0 보다 크고 보유 잔액 이하의 포인트를 사용하려 하면 포인트 사용에 성공한다.")
    @Test
    void use_success() {
        long initialAmount = 3_000_000L;
        long useAmount = 1_000_000L;

        Point point = Point.of(2L, initialAmount);

        point.use(useAmount);

        long expectedAmount = initialAmount - useAmount;
        assertThat(point.getBalance()).isEqualTo(expectedAmount);
    }

}
