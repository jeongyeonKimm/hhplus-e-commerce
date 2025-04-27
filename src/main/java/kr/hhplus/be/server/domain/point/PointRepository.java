package kr.hhplus.be.server.domain.point;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PointRepository {

    Point savePoint(Point point);

    Optional<Point> findPointByUserId(Long userId);

    void savePointHistory(PointHistory pointHistory);

    Optional<Point> findPointByUserIdWithLock(Long userId);

    Boolean existsByPointIdAndAmountAndTypeAndCreatedAtAfter(Long userId, Long amount, TransactionType type, LocalDateTime localDateTime);
}
