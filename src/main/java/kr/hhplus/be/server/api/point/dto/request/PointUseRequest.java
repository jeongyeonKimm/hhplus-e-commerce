package kr.hhplus.be.server.api.point.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "포인트 사용(결제) 요청 DTO")
@Getter
public class PointUseRequest {

    @Schema(description = "주문 ID")
    private Long orderId;

}
