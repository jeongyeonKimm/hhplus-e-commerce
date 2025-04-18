package kr.hhplus.be.server.interfaces.api.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.application.order.dto.OrderProductList;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
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

    @Size(min = 1)
    @Schema(description = "주문 상품 목록")
    private List<OrderProductRequest> orderProducts;

    @Builder
    private OrderRequest(Long userId, Long userCouponId, List<OrderProductRequest> orderProducts) {
        this.userId = userId;
        this.userCouponId = userCouponId;
        this.orderProducts = orderProducts;
    }

    public OrderCreateCommand toOrderCreateCommand() {
        List<OrderProductInfo> infos = new ArrayList<>();
        for (OrderProductRequest request : orderProducts) {
            infos.add(request.toOrderProductInfo());
        }

        OrderProductList orderProductList = OrderProductList.of(infos);

        return OrderCreateCommand.of(userId, userCouponId, orderProductList);
    }
}
