package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.Builder;

import static kr.hhplus.be.server.common.exception.ErrorCode.CHARGE_AMOUNT_EXCEEDS_LIMIT;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_CHARGE_AMOUNT;

public class Point {

    private static final int MAX_TOTAL_CHARGE_AMOUNT = 5_000_000;
    private static final int MAX_CHARGE_AMOUNT_PER_TRANSACTION = 1_000_000;

    private Long id;
    private Long userId;
    private Integer balance;

    @Builder
    private Point(Long id, Long userId, Integer balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }

    public Point charge(int amount) {
        if (amount <= 0) {
            throw new ApiException(INVALID_CHARGE_AMOUNT);
        }

        if (amount > MAX_CHARGE_AMOUNT_PER_TRANSACTION) {
            throw new ApiException(INVALID_CHARGE_AMOUNT);
        }

        int newAmount = balance + amount;
        if (newAmount > MAX_TOTAL_CHARGE_AMOUNT) {
            throw new ApiException(CHARGE_AMOUNT_EXCEEDS_LIMIT);
        }

        return new Point(id, userId, newAmount);
    }
}
