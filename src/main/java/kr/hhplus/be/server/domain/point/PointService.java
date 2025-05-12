package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.support.aop.lock.DistributedLock;
import kr.hhplus.be.server.support.aop.lock.LockType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;
import static kr.hhplus.be.server.domain.point.TransactionType.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PointService {

    private final PointRepository pointRepository;

    @DistributedLock(key = "'user:' + #userId", type = LockType.SPIN_LOCK)
    public Point chargePoint(Long userId, Long amount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseGet(() -> Point.of(userId, 0L));

        Boolean isDuplicate = pointRepository.existsByPointIdAndAmountAndTypeAndCreatedAtAfter(point.getId(), amount, CHARGE, LocalDateTime.now().minusSeconds(1));
        if (isDuplicate) {
            throw new ApiException(DUPLICATE_CHARGE);
        }

        point.charge(amount);
        Point savedPoint = pointRepository.savePoint(point);

        PointHistory history = PointHistory.of(savedPoint, amount, CHARGE);
        pointRepository.savePointHistory(history);

        return savedPoint;
    }

    public Point getPoint(Long userId) {
        return pointRepository.findPointByUserId(userId)
                .orElseGet(() -> Point.of(userId, 0L));
    }

    @DistributedLock(key = "'user:' + #userId", type = LockType.SPIN_LOCK)
    public Point usePoint(Long userId, Long amount) {
        Point point = pointRepository.findPointByUserId(userId)
                .orElseThrow(() -> new ApiException(POINT_NOT_EXIST));

        point.use(amount);
        Point savedPoint = pointRepository.savePoint(point);

        Boolean isDuplicate = pointRepository.existsByPointIdAndAmountAndTypeAndCreatedAtAfter(savedPoint.getId(), amount, USE, LocalDateTime.now().minusSeconds(1));
        if (isDuplicate) {
            throw new ApiException(DUPLICATE_USE);
        }

        PointHistory history = PointHistory.of(point, amount, USE);
        pointRepository.savePointHistory(history);

        return point;
    }

    @Transactional
    public void rollbackPoint(Long userId, Long totalAmount) {
        Point point = pointRepository.findPointByUserIdWithLock(userId)
                .orElseThrow(() -> new ApiException(POINT_NOT_EXIST));

        point.restore(totalAmount);
        pointRepository.savePoint(point);

        PointHistory history = PointHistory.of(point, totalAmount, ROLLBACK);
        pointRepository.savePointHistory(history);
    }

}
