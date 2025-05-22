package kr.hhplus.be.server.interfaces.api.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "주문 요청 DTO")
@Getter
public class OrderRequest {

    @Positive(message = "사용자 ID는 양수입니다.")
    @Schema(description = "사용자 ID")
    private Long userId;


    private Long couponId;

    @Positive(message = "사용자 쿠폰 ID는 양수입니다.")
    @Schema(description = "사용자 쿠폰 ID")
    private Long userCouponId;

    @Valid
    @Size(min = 1, message = "주문 상품은 1개 이상 있어야 합니다.")
    @Schema(description = "주문 상품 목록")
    private List<OrderProductRequest> orderProducts;

    private OrderRequest(Long userId, Long userCouponId, List<OrderProductRequest> orderProducts) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderProducts = orderProducts;
    }

    public static OrderRequest of(Long userId, Long userCouponId, List<OrderProductRequest> orderProducts) {
        return new OrderRequest(userId, userCouponId, orderProducts);
    }

    public OrderCreateCommand toOrderCreateCommand() {
        List<OrderProductInfo> infos = new ArrayList<>();
        for (OrderProductRequest request : orderProducts) {
            infos.add(request.toOrderProductInfo());
        }

        return OrderCreateCommand.of(userId, userCouponId, infos);
    }
}
