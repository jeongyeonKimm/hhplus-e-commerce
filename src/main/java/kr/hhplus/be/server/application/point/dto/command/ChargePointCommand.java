package kr.hhplus.be.server.application.point.dto.command;

import lombok.Getter;

@Getter
public class ChargePointCommand {

    private Long userId;
    private Long chargeAmount;

    private ChargePointCommand(Long userId, Long chargeAmount) {
        this.userId = userId;
        this.chargeAmount = chargeAmount;
    }

    public static ChargePointCommand of(Long userId, Long chargeAmount) {
        return new ChargePointCommand(userId, chargeAmount);
    }
}
