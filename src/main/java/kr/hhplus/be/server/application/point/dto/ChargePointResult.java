package kr.hhplus.be.server.application.point.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChargePointResult {

    private Long userId;
    private Integer balance;

    @Builder
    public ChargePointResult(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }
}
