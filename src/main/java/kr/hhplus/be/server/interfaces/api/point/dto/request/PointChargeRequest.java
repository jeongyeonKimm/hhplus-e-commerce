package kr.hhplus.be.server.interfaces.api.point.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.point.dto.command.ChargePointCommand;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포인트 충전 요청 DTO")
@Getter
public class PointChargeRequest {

    @Positive(message = "사용자 ID는 양수입니다.")
    @Schema(description = "사용자 ID")
    private Long userId;

    @Positive(message = "충전 금액은 양수입니다.")
    @Max(value = 1000000)
    @Schema(description = "충전 금액")
    private Long chargeAmount;

    @Builder
    private PointChargeRequest(Long userId, Long chargeAmount) {
        this.userId = userId;
        this.chargeAmount = chargeAmount;
    }

    public ChargePointCommand toChargeCommand() {
        return ChargePointCommand.of(userId, chargeAmount);
    }
}
