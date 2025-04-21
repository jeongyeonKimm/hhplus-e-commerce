package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.common.exception.ErrorCode.POINT_NOT_EXIST;
import static kr.hhplus.be.server.domain.point.TransactionType.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public Point chargePoint(Long userId, Long amount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseGet(() -> Point.of(userId, 0L));

        point.charge(amount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.saveHistory(point, amount, CHARGE);
        pointRepository.savePointHistory(history);

        return point;
    }

    public Point getPoint(Long userId) {
        return pointRepository.findPointByUserId(userId)
                .orElseGet(() -> Point.of(userId, 0L));
    }

    @Transactional
    public Point usePoint(Long userId, Long amount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseThrow(() -> new ApiException(POINT_NOT_EXIST));

        point.use(amount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.saveHistory(point, amount, USE);
        pointRepository.savePointHistory(history);

        return point;
    }

    @Transactional
    public void rollbackPoint(Long userId, Long totalAmount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseThrow(() -> new ApiException(POINT_NOT_EXIST));

        point.restore(totalAmount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.saveHistory(point, totalAmount, ROLLBACK);
        pointRepository.savePointHistory(history);
    }

}
