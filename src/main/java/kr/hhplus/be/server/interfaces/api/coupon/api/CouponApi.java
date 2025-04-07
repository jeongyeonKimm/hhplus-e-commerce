package kr.hhplus.be.server.interfaces.api.coupon.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.interfaces.api.coupon.dto.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Coupon", description = "Coupon API")
public interface CouponApi {

    @Operation(summary = "주문", description = "주문 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "성공적으로 조회됨",
            content = @Content(mediaType = "application/json")
    )
    ApiResponse<CouponListResponse> getCoupons(@Positive @RequestParam Long userId);

    @Operation(summary = "주문", description = "주문 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 발급됨",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "쿠폰 잔여 수량 부족",
                                            summary = "쿠폰 잔여 수량 부족",
                                            value = """
                                                    {
                                                        "code": 409,
                                                        "message": "쿠폰의 잔여 수량이 부족합니다."
                                                      }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "이미 발급 받은 쿠폰",
                                            summary = "이미 발급 받은 쿠폰",
                                            value = """
                                                    {
                                                        "code": 409,
                                                        "message": "이미 쿠폰을 발급 받았습니다."
                                                      }
                                                    """
                                    )
                            }
                    )
            )
    })
    ApiResponse<List<?>> issueCoupon(@Valid  @RequestBody CouponIssueRequest request);
}
