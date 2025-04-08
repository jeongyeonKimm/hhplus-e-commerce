package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.point.dto.ChargePointCommand;
import kr.hhplus.be.server.application.point.dto.ChargePointResult;
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

        return ChargePointResult.builder()
                .userId(point.getUserId())
                .balance(point.getBalance())
                .build();
    }
}
