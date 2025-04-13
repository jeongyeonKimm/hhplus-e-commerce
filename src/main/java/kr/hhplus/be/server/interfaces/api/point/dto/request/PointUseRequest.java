package kr.hhplus.be.server.interfaces.api.point.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import lombok.Getter;

@Schema(description = "포인트 사용(결제) 요청 DTO")
@Getter
public class PointUseRequest {

    @Positive
    @Schema(description = "주문 ID")
    private Long orderId;

    private PointUseRequest(Long orderId) {
        this.orderId = orderId;
    }

    public static PointUseRequest of(Long orderId) {
        return new PointUseRequest(orderId);
    }

    public PaymentCommand toPaymentCommand() {
        return PaymentCommand.of(this.orderId);
    }
}
