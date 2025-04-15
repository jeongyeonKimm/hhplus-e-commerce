package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.point.dto.command.ChargePointCommand;
import kr.hhplus.be.server.application.point.dto.command.GetPointCommand;
import kr.hhplus.be.server.application.point.dto.command.UsePointCommand;
import kr.hhplus.be.server.application.point.dto.result.PointResult;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointFacadeTest {

    @InjectMocks
    private PointFacade pointFacade;

    @Mock
    private PointService pointService;

    @DisplayName("포인트 충전 시 PointService를 통해 포인트가 충전되고 충전된 포인트가 반환된다.")
    @Test
    void charge() {
        long userId = 1L;
        long chargeAmount = 5000L;
        ChargePointCommand command = ChargePointCommand.of(userId, chargeAmount);

        Point point = Point.of(userId, 10000L);
        given(pointService.chargePoint(userId, chargeAmount)).willReturn(point);

        PointResult result = pointFacade.charge(command);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(10000);

        verify(pointService, times(1)).chargePoint(userId, chargeAmount);
    }

    @DisplayName("포인트 조회 시 PointService를 통해 포인트가 조회되고 조회된 결과가 된다.")
    @Test
    void getPoint() {
        long userId = 1L;
        GetPointCommand command = GetPointCommand.of(userId);

        Point point = Point.of(userId, 10000L);
        given(pointService.getPoint(userId)).willReturn(point);

        PointResult result = pointFacade.getPoint(command);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(10000);

        verify(pointService, times(1)).getPoint(userId);
    }

    @DisplayName("포인트 사용 시 PointService를 통해 포인트가 사용되고 사용된 결과가 된다.")
    @Test
    void use() {
        long userId = 1L;
        long useAmount = 5000;
        UsePointCommand command = UsePointCommand.of(userId, useAmount);

        Point point = Point.of(userId, 10000L);
        given(pointService.usePoint(userId, useAmount)).willReturn(point);

        PointResult result = pointFacade.use(command);

        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getBalance()).isEqualTo(10000);

        verify(pointService, times(1)).usePoint(userId, useAmount);
    }

}
