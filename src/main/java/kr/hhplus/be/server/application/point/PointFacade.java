package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.point.dto.command.ChargePointCommand;
import kr.hhplus.be.server.application.point.dto.command.UsePointCommand;
import kr.hhplus.be.server.application.point.dto.result.PointResult;
import kr.hhplus.be.server.application.point.dto.command.GetPointCommand;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.pointhistory.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointFacade {

    private final PointService pointService;
    private final PointHistoryService pointHistoryService;

    public PointResult charge(ChargePointCommand command) {
        Point point = pointService.chargePoint(command.getUserId(), command.getChargeAmount());

        pointHistoryService.saveChargeHistory(point.getId(), command.getChargeAmount(), point.getBalance());

        return PointResult.from(point);
    }

    public PointResult getPoint(GetPointCommand command) {
        Point point = pointService.getPoint(command.getUserId());

        return PointResult.from(point);
    }

    public PointResult use(UsePointCommand command) {
        Point point = pointService.usePoint(command.getUserId(), command.getUseAmount());

        pointHistoryService.saveUseHistory(point.getId(), command.getUseAmount(), point.getBalance());

        return PointResult.from(point);
    }
}
