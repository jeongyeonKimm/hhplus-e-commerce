package kr.hhplus.be.server.api.order.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequest {

    private Long userId;
    private Long userCouponId;
    private List<OrderItemRequest> orderItems;
}
