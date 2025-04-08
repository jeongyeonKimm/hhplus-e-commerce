package kr.hhplus.be.server.application.point.dto;

import lombok.Getter;

@Getter
public class ChargePointCommand {

    private Long userId;
    private Integer chargeAmount;
}
