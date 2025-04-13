package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.common.exception.ErrorCode.POINT_NOT_EXIST;
import static kr.hhplus.be.server.domain.point.TransactionType.*;

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointRepository pointRepository;
    private long pointSequence = 1L;
    private long pointHistorySequence = 1L;

    public Point chargePoint(Long userId, Long amount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseGet(() -> Point.of(generatePointId(), userId, 0L));

        point.charge(amount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.saveHistory(generatePointHistoryId(), point.getId(), amount, point.getBalance(), CHARGE);
        pointRepository.savePointHistory(history);

        return point;
    }

    public Point getPoint(Long userId) {
        return pointRepository.findPointByUserId(userId)
                .orElseGet(() -> Point.of(generatePointId(), userId, 0L));
    }

    public Point usePoint(Long userId, Long amount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseThrow(() -> new ApiException(POINT_NOT_EXIST));

        point.use(amount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.saveHistory(generatePointHistoryId(), point.getId(), amount, point.getBalance(), USE);
        pointRepository.savePointHistory(history);

        return point;
    }

    public void rollbackPoint(Long userId, Long totalAmount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseThrow(() -> new ApiException(POINT_NOT_EXIST));

        point.restore(totalAmount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.saveHistory(generatePointHistoryId(), point.getId(), totalAmount, point.getBalance(), ROLLBACK);
        pointRepository.savePointHistory(history);
    }

    private Long generatePointId() {
        return pointSequence++;
    }

    private Long generatePointHistoryId() {
        return pointHistorySequence++;
    }
}
