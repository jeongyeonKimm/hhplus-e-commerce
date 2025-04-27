package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.point.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;
    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Point savePoint(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public Optional<Point> findPointByUserId(Long userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public void savePointHistory(PointHistory pointHistory) {
        pointHistoryJpaRepository.save(pointHistory);
    }

    @Override
    public Optional<Point> findPointByUserIdWithLock(Long userId) {
        return pointJpaRepository.findByUserIdWithPessimisticLock(userId);
    }

    @Override
    public Boolean existsByPointIdAndAmountAndTypeAndCreatedAtAfter(Long pointId, Long amount, TransactionType type, LocalDateTime createdAt) {
        return pointHistoryJpaRepository.existsByPointIdAndAmountAndTypeAndCreatedAtAfter(pointId, amount, type, createdAt);
    }
}
