package kr.hhplus.be.server.application.point.dto.result;

import kr.hhplus.be.server.domain.point.Point;
import lombok.Getter;

@Getter
public class ChargePointResult {

    private Long userId;
    private Integer balance;

    public ChargePointResult(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static ChargePointResult from(Point point) {
        return new ChargePointResult(point.getUserId(), point.getBalance());
    }
}
