package kr.hhplus.be.server.application.point.dto.result;

import kr.hhplus.be.server.domain.point.Point;
import lombok.Getter;

@Getter
public class GetPointResult {

    private Long userId;
    private Integer balance;

    public GetPointResult(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static GetPointResult from(Point point) {
        return new GetPointResult(point.getUserId(), point.getBalance());
    }
}
