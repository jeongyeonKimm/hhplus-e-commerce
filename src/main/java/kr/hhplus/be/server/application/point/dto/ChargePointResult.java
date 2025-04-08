package kr.hhplus.be.server.application.point.dto;

import lombok.Builder;

public class ChargePointResult {

    private Long id;
    private Long userId;
    private Integer balance;

    @Builder
    public ChargePointResult(Long id, Long userId, Integer balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
    }
}
