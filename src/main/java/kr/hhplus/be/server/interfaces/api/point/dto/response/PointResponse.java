package kr.hhplus.be.server.interfaces.api.point.dto.response;

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
}
