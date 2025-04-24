package kr.hhplus.be.server.infrastructure.point;

import kr.hhplus.be.server.domain.point.PointHistory;
import kr.hhplus.be.server.domain.point.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {

    Boolean existsByPointIdAndAmountAndTypeAndCreatedAtAfter(Long pointId, Long amount, TransactionType type, LocalDateTime createdAt);
}
