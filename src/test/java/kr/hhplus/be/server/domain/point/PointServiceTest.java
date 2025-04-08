package kr.hhplus.be.server.domain.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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
    }
}
