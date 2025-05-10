package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point_history")
@Entity
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pointId;

    private Long amount;

    private Long balance;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private PointHistory(Long pointId, Long amount, Long balance, TransactionType type) {
        this.pointId = pointId;
        this.amount = amount;
        this.balance = balance;
        this.type = type;
    }

    public static PointHistory of(Point point, Long amount, TransactionType type) {
        return new PointHistory(point.getId(), amount, point.getBalance(), type);
    }
}
