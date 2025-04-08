package kr.hhplus.be.server.interfaces.api.point.dto.response;

import kr.hhplus.be.server.application.point.dto.ChargePointResult;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PointResponse {

    private Long userId;
    private Integer balance;

    @Builder
    private PointResponse(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public static PointResponse from(ChargePointResult result) {
        return PointResponse.builder()
                .userId(result.getUserId())
                .balance(result.getBalance())
                .build();
    }
}
