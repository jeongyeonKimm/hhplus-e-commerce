package kr.hhplus.be.server.interfaces.api.point.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "포인트 충전 요청 DTO")
@Getter
public class PointChargeRequest {

    @Positive
    @Schema(description = "사용자 ID")
    private Long userId;

    @Positive
    @Max(value = 1000000)
    @Schema(description = "충전 금액")
    private Integer chargeAmount;

    @Builder
    private PointChargeRequest(Long userId, Integer chargeAmount) {
        this.userId = userId;
        this.chargeAmount = chargeAmount;
    }
}
