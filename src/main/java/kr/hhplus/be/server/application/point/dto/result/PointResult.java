package kr.hhplus.be.server.application.point.dto.result;

import kr.hhplus.be.server.domain.point.Point;
import lombok.Getter;

@Getter
public class PointResult {

    private Long userId;
    private Long balance;

    private PointResult(Long userId, Long balance) {
        this.userId = userId;
        this.balance = balance;
    }


    public static PointResult from(Point point) {
        return new PointResult(point.getUserId(), point.getBalance());
    }
}
