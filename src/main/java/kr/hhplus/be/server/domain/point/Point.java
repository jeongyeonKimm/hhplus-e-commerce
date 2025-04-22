package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point")
@Entity
public class Point extends BaseEntity {

    private static final int MAX_TOTAL_CHARGE_AMOUNT = 5_000_000;
    private static final int MAX_CHARGE_AMOUNT_PER_TRANSACTION = 1_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long balance;

    @Version
    private Long version;

    private Point(Long userId, Long balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static Point of(Long userId, Long balance) {
        return new Point(userId, balance);
    }

    public void charge(Long amount) {
        if (amount <= 0) {
            throw new ApiException(INVALID_CHARGE_AMOUNT);
        }

        if (amount > MAX_CHARGE_AMOUNT_PER_TRANSACTION) {
            throw new ApiException(INVALID_CHARGE_AMOUNT);
        }

        long newAmount = this.balance + amount;
        if (newAmount > MAX_TOTAL_CHARGE_AMOUNT) {
            throw new ApiException(CHARGE_AMOUNT_EXCEEDS_LIMIT);
        }

        this.balance += amount;
    }

    public void use(Long amount) {
        if (amount <= 0) {
            throw new ApiException(INVALID_USE_AMOUNT);
        }

        if (amount > balance) {
            throw new ApiException(INVALID_USE_AMOUNT);
        }

        this.balance -= amount;
    }

    public void restore(Long restoreAmount) {
        if (restoreAmount <= 0) {
            throw new ApiException(INVALID_RESTORE_AMOUNT);
        }

        this.balance += restoreAmount;
    }
}
