package kr.hhplus.be.server.interfaces.api.order;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.order.dto.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.dto.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@RestController
public class OrderController implements OrderSpec {

    private final OrderFacade orderFacade;

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResult result = orderFacade.order(request.toOrderCreateCommand());
        return ApiResponse.successWithCreated(OrderResponse.from(result));
    }
}
