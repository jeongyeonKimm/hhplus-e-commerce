package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.Getter;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;

@Getter
public class Point {

    private static final int MAX_TOTAL_CHARGE_AMOUNT = 5_000_000;
    private static final int MAX_CHARGE_AMOUNT_PER_TRANSACTION = 1_000_000;

    private Long id;
    private Long userId;
    private Integer balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Point(Long id, Long userId, Integer balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Point of(Long id, Long userId, Integer balance) {
        return new Point(id, userId, balance);
    }

    public void charge(int amount) {
        if (amount <= 0) {
            throw new ApiException(INVALID_CHARGE_AMOUNT);
        }

        if (amount > MAX_CHARGE_AMOUNT_PER_TRANSACTION) {
            throw new ApiException(INVALID_CHARGE_AMOUNT);
        }

        int newAmount = this.balance + amount;
        if (newAmount > MAX_TOTAL_CHARGE_AMOUNT) {
            throw new ApiException(CHARGE_AMOUNT_EXCEEDS_LIMIT);
        }

        this.balance += amount;
    }

    public void use(int amount) {
        if (amount <= 0) {
            throw new ApiException(INVALID_USE_AMOUNT);
        }

        if (amount > balance) {
            throw new ApiException(INVALID_USE_AMOUNT);
        }

        this.balance -= amount;
    }
}
