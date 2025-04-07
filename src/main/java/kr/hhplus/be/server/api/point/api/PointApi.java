package kr.hhplus.be.server.api.point.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.api.point.dto.request.PointChargeRequest;
import kr.hhplus.be.server.api.point.dto.request.PointUseRequest;
import kr.hhplus.be.server.api.point.dto.response.PointResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Point", description = "Point API")
public interface PointApi {

    @Operation(summary = "포인트 충전", description = "포인트 충전 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공적으로 충전됨",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "1회 충전 금액 초과",
                                            summary = "1회 충전 금액 초과",
                                            value = """
                                                    {
                                                      "code": 409,
                                                      "message": "1회 충전 금액은 1,000,000원을 초과할 수 없습니다. 입력값 : 1,500,000원"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "누적 충전 금액 초과",
                                            summary = "누적 충전 금액 초과",
                                            value = """
                                                    {
                                                      "code": 409,
                                                      "message": "누적 충전 금액은 5,000,000원을 초과할 수 없습니다. 현재 누적 충전 금액 : 5,000,000원"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ApiResponse<PointResponse> chargePoint(@Valid @RequestBody PointChargeRequest request);

    @Operation(summary = "포인트 조회", description = "포인트 조회 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "포인트 조회됨",
            content = @Content(mediaType = "application/json")
    )
    @Parameter(name = "userId", description = "사용자 ID")
    ApiResponse<PointResponse> getPoint(@Positive @RequestParam Long userId);

    @Operation(summary = "포인트 사용(결제)", description = "포인트 사용(결제) API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "204",
                    description = "성공적으로 결제됨",
                    content = @Content(mediaType = "application/json")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "포인트 잔액 부족",
                                            summary = "포인트 잔액 부족",
                                            value = """
                                                    {
                                                      "code": 409,
                                                      "message": "포인트 잔액이 부족합니다. 현재 잔액 : 100,000원, 결제 금액 : 200,000원"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "주문 만료로 결제 불가",
                                            summary = "주문 만료로 결제 불가",
                                            value = """
                                                    {
                                                      "code": 409,
                                                      "message": "주문 상태가 EXPIRED(결제 불가 건)입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    ApiResponse<PointResponse> usePoint(@Valid @RequestBody PointUseRequest request);
}
