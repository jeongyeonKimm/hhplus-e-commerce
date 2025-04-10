package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.common.exception.ErrorCode.POINT_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointRepository pointRepository;
    private long pointSequence = 1L;
    private long pointHistorySequence = 1L;

    public Point chargePoint(Long userId, Integer amount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseGet(() -> Point.create(generatePointId(), userId, 0));

        point.charge(amount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.charge(generatePointHistoryId(), point.getId(), amount, point.getBalance());
        pointRepository.savePointHistory(history);

        return point;
    }

    public Point getPoint(Long userId) {
        return pointRepository.findPointByUserId(userId)
                .orElseGet(() -> Point.create(generatePointId(), userId, 0));
    }

    public Point usePoint(Long userId, Integer amount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseThrow(() -> new ApiException(POINT_NOT_EXIST));

        point.use(amount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.use(generatePointHistoryId(), point.getId(), amount, point.getBalance());
        pointRepository.savePointHistory(history);

        return point;
    }

    private Long generatePointId() {
        return pointSequence++;
    }

    private Long generatePointHistoryId() {
        return pointHistorySequence++;
    }
}
