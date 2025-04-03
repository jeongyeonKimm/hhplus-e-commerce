package kr.hhplus.be.server.api.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.List;

@Schema(description = "주문 요청 DTO")
@Getter
public class OrderRequest {

    @Positive
    @Schema(description = "사용자 ID")
    private Long userId;

    @Positive
    @Schema(description = "사용자 쿠폰 ID")
    private Long userCouponId;

    @Schema(description = "주문 상품 목록")
    private List<OrderProductRequest> orderProducts;
}
