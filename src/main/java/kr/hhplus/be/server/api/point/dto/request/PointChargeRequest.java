package kr.hhplus.be.server.api.point.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "포인트 충전 요청 DTO")
@Getter
public class PointChargeRequest {

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "충전 금액")
    private Integer chargeAmount;

}
