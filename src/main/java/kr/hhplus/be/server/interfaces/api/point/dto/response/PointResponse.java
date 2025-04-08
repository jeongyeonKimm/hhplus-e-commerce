package kr.hhplus.be.server.interfaces.api.point.dto.response;

import kr.hhplus.be.server.application.point.dto.result.ChargePointResult;
import kr.hhplus.be.server.application.point.dto.result.GetPointResult;
import lombok.Getter;

@Getter
public class PointResponse {

    private Long userId;
    private Integer balance;

    private PointResponse(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static PointResponse from(ChargePointResult result) {
        return new PointResponse(result.getUserId(), result.getBalance());
    }

    public static PointResponse from(GetPointResult result) {
        return new PointResponse(result.getUserId(), result.getBalance());
    }
}
