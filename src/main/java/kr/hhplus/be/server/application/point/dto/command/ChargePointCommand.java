package kr.hhplus.be.server.application.point.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChargePointCommand {

    private Long userId;
    private Integer chargeAmount;

    @Builder
    private ChargePointCommand(Long userId, Integer chargeAmount) {
        this.userId = userId;
        this.chargeAmount = chargeAmount;
    }
}
