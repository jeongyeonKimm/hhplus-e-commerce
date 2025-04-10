package kr.hhplus.be.server.application.payment.dto;

import lombok.Getter;

@Getter
public class PaymentCommand {

    private Long orderId;

    private PaymentCommand(Long orderId) {
        this.orderId = orderId;
    }

    public static PaymentCommand of(Long orderId) {
        return new PaymentCommand(orderId);
    }
}
