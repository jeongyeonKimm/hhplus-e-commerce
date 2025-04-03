package kr.hhplus.be.server.api.order;

import kr.hhplus.be.server.api.order.api.OrderApi;
import kr.hhplus.be.server.api.order.dto.request.OrderRequest;
import kr.hhplus.be.server.api.order.dto.response.OrderResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderMockController implements OrderApi {

    @PostMapping("/api/v1/orders")
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        OrderResponse response = OrderResponse.builder()
                .orderId(10L)
                .build();
        return ApiResponse.successWithCreated(response);
    }
}
