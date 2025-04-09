package kr.hhplus.be.server.application.payment.dto;

import lombok.Getter;

@Getter
public class PaymentCommand {

    private Long userId;
    private Long orderId;
    private Integer useAmount;
}
