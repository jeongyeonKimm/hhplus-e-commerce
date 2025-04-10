package kr.hhplus.be.server.application.point.dto.result;

import kr.hhplus.be.server.domain.point.Point;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PointResult {

    private Long userId;
    private Integer balance;

    @Builder
    public PointResult(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static PointResult from(Point point) {
        return PointResult.builder()
                .userId(point.getUserId())
                .balance(point.getBalance())
                .build();
    }
}
