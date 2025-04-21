package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.point.dto.command.ChargePointCommand;
import kr.hhplus.be.server.application.point.dto.command.GetPointCommand;
import kr.hhplus.be.server.application.point.dto.command.UsePointCommand;
import kr.hhplus.be.server.application.point.dto.result.PointResult;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointFacade {

    private final PointService pointService;

    public PointResult charge(ChargePointCommand command) {
        Point point = pointService.chargePoint(command.getUserId(), command.getChargeAmount());
        return PointResult.from(point);
    }

    public PointResult getPoint(GetPointCommand command) {
        Point point = pointService.getPoint(command.getUserId());
        return PointResult.from(point);
    }

    public PointResult use(UsePointCommand command) {
        Point point = pointService.usePoint(command.getUserId(), command.getUseAmount());
        return PointResult.from(point);
    }
}
