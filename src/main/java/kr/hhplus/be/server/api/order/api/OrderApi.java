package kr.hhplus.be.server.api.order.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.api.order.dto.request.OrderRequest;
import kr.hhplus.be.server.api.order.dto.response.OrderResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Order", description = "Order API")
public interface OrderApi {

    @Operation(summary = "주문", description = "주문 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "성공적으로 주문됨",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "보유하지 않은 쿠폰",
                                            summary = "보유하지 않은 쿠폰",
                                            value = """
                                                    {
                                                       "code": 409,
                                                       "message": "사용자가 보유한 쿠폰이 아닙니다."
                                                     }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "유효기간이 지난 쿠폰",
                                            summary = "유효기간이 지난 쿠폰",
                                            value = """
                                                    {
                                                       "code": 409,
                                                       "message": "쿠폰이 유효한 기간이 아닙니다."
                                                     }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 사용된 쿠폰",
                                            summary = "이미 사용된 쿠폰",
                                            value = """
                                                    {
                                                        "code": 409,
                                                        "message": "이미 사용된 쿠폰입니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "재고 부족",
                                            summary = "재고 부족",
                                            value = """
                                                    {
                                                        "code": 409,
                                                        "message": "상품의 재고가 부족합니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ApiResponse<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request);
}
