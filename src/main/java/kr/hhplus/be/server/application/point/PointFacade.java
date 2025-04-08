package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.point.dto.command.ChargePointCommand;
import kr.hhplus.be.server.application.point.dto.result.ChargePointResult;
import kr.hhplus.be.server.application.point.dto.command.GetPointCommand;
import kr.hhplus.be.server.application.point.dto.result.GetPointResult;
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

    public ChargePointResult charge(ChargePointCommand command) {
        Point point = pointService.chargePoint(command.getUserId(), command.getChargeAmount());

        pointHistoryService.saveChargeHistory(point.getId(), command.getChargeAmount(), point.getBalance());

        return ChargePointResult.from(point);
    }

    public GetPointResult getPoint(GetPointCommand command) {
        Point point = pointService.getPoint(command.getUserId());

        return GetPointResult.from(point);
    }
}
