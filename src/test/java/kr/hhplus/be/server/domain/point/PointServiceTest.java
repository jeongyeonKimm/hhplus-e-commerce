package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorCode.POINT_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
        Long userId = 1L;
        int chargeAmount = 1000;

        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(pointRepository.save(any(Point.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Point point = pointService.chargePoint(userId, chargeAmount);

        assertThat(point.getUserId()).isEqualTo(userId);
        assertThat(point.getBalance()).isEqualTo(chargeAmount);

        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, times(1)).save(any(Point.class));
    }

    @DisplayName("포인트 충전이 처음이 아닌 경우 포인트가 기존에 보유 중인 포인트에 누적이 된다.")
    @Test
    void chargePoint() {
        Long userId = 1L;
        int initialPoint = 1000;
        int chargeAmount = 3000;

        Point point = Point.create(2L, userId, initialPoint);

        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));
        given(pointRepository.save(any(Point.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Point chargedPoint = pointService.chargePoint(userId, chargeAmount);

        int expectedPoint = initialPoint + chargeAmount;
        assertThat(chargedPoint.getUserId()).isEqualTo(userId);
        assertThat(chargedPoint.getBalance()).isEqualTo(expectedPoint);

        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, times(1)).save(any(Point.class));
    }

    @DisplayName("포인트가 존재하지 않으면 포인트 사용에 실패한다.")
    @Test
    void usePoint_whenNotExist() {
        long userId = 1L;
        int useAmount = 1000;
        given(pointRepository.findByUserId(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> pointService.usePoint(userId, useAmount))
                .isInstanceOf(ApiException.class)
                .hasMessage(POINT_NOT_EXIST.getMessage());
    }

    @DisplayName("포인트가 존재하는 경우 기존 보유 중인 포인트에서 사용 금액 만큼 차감된다.")
    @Test
    void usePoint() {
        Long userId = 1L;
        int initialPoint = 3000;
        int useAmount = 1000;

        Point point = Point.create(2L, userId, initialPoint);

        given(pointRepository.findByUserId(userId)).willReturn(Optional.of(point));
        given(pointRepository.save(any(Point.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        Point chargedPoint = pointService.usePoint(userId, useAmount);

        int expectedPoint = initialPoint - useAmount;
        assertThat(chargedPoint.getUserId()).isEqualTo(userId);
        assertThat(chargedPoint.getBalance()).isEqualTo(expectedPoint);

        verify(pointRepository, times(1)).findByUserId(userId);
        verify(pointRepository, times(1)).save(any(Point.class));
    }
}
