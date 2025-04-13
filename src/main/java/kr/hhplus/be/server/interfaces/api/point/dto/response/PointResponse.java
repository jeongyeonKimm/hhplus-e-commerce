package kr.hhplus.be.server.interfaces.api.point.dto.response;

import kr.hhplus.be.server.application.point.dto.result.PointResult;
import lombok.Getter;

@Getter
public class PointResponse {

    private Long userId;
    private Integer balance;

    private PointResponse(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static PointResponse from(PointResult result) {
        return new PointResponse(result.getUserId(), result.getBalance());
    }
}
