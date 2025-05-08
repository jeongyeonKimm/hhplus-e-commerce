package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorCode.POINT_NOT_EXIST;
import static kr.hhplus.be.server.domain.point.TransactionType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    @DisplayName("포인트 충전을 처음하는 경우 포인트가 새로 생성되고 충전이 된다.")
    @Test
    void chargePoint_whenFirstTime() {
        long userId = 1L;
        long chargeAmount = 1000L;

        Point point = Instancio.of(Point.class)
                .set(field("id"), 2L)
                .set(field("userId"), userId)
                .set(field("balance"), chargeAmount)
                .create();

        given(pointRepository.findPointByUserId(userId)).willReturn(Optional.empty());
        given(pointRepository.existsByPointIdAndAmountAndTypeAndCreatedAtAfter(eq(null), eq(chargeAmount), eq(CHARGE), any(LocalDateTime.class)))
                .willReturn(false);
        given(pointRepository.savePoint(any(Point.class))).willReturn(point);

        Point chargePoint = pointService.chargePoint(userId, chargeAmount);

        assertThat(chargePoint.getUserId()).isEqualTo(userId);
        assertThat(chargePoint.getBalance()).isEqualTo(chargeAmount);

        verify(pointRepository, times(1)).findPointByUserId(userId);
        verify(pointRepository, times(1)).savePoint(any(Point.class));
        verify(pointRepository, times(1))
                .existsByPointIdAndAmountAndTypeAndCreatedAtAfter(eq(null), eq(chargeAmount), eq(CHARGE), any(LocalDateTime.class));
    }

    @DisplayName("포인트 충전이 처음이 아닌 경우 포인트가 기존에 보유 중인 포인트에 누적이 된다.")
    @Test
    void chargePoint() {
        long userId = 1L;
        long initialPoint = 1000L;
        long chargeAmount = 3000L;

        Point point = Point.of(userId, initialPoint);

        given(pointRepository.findPointByUserId(userId)).willReturn(Optional.of(point));
        given(pointRepository.savePoint(any(Point.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Point chargedPoint = pointService.chargePoint(userId, chargeAmount);

        long expectedPoint = initialPoint + chargeAmount;
        assertThat(chargedPoint.getUserId()).isEqualTo(userId);
        assertThat(chargedPoint.getBalance()).isEqualTo(expectedPoint);

        verify(pointRepository, times(1)).findPointByUserId(userId);
        verify(pointRepository, times(1)).savePoint(any(Point.class));
    }

    @DisplayName("포인트가 존재하지 않으면 포인트 사용에 실패한다.")
    @Test
    void usePoint_whenNotExist() {
        long userId = 1L;
        long useAmount = 1000L;
        given(pointRepository.findPointByUserId(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> pointService.usePoint(userId, useAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(POINT_NOT_EXIST.getMessage());
    }

    @DisplayName("포인트가 존재하는 경우 기존 보유 중인 포인트에서 사용 금액 만큼 차감된다.")
    @Test
    void usePoint() {
        long userId = 1L;
        long initialPoint = 3000L;
        long useAmount = 1000L;

        Point point = Point.of(userId, initialPoint);

        given(pointRepository.findPointByUserId(userId)).willReturn(Optional.of(point));
        given(pointRepository.savePoint(any(Point.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Point chargedPoint = pointService.usePoint(userId, useAmount);

        long expectedPoint = initialPoint - useAmount;
        assertThat(chargedPoint.getUserId()).isEqualTo(userId);
        assertThat(chargedPoint.getBalance()).isEqualTo(expectedPoint);

        verify(pointRepository, times(1)).findPointByUserId(userId);
        verify(pointRepository, times(1)).savePoint(any(Point.class));
    }
}
