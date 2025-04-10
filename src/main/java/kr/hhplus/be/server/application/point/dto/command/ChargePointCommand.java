package kr.hhplus.be.server.application.point.dto.command;

import lombok.Getter;

@Getter
public class ChargePointCommand {

    private Long userId;
    private Integer chargeAmount;

    private ChargePointCommand(Long userId, Integer chargeAmount) {
        this.userId = userId;
        this.chargeAmount = chargeAmount;
    }

    public static ChargePointCommand of(long userId, int chargeAmount) {
        return new ChargePointCommand(userId, chargeAmount);
    }
}
