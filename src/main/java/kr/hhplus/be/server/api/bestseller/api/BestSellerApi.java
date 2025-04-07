package kr.hhplus.be.server.api.bestseller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.bestseller.dto.response.BestProductListResponse;
import kr.hhplus.be.server.api.bestseller.dto.response.BestProductResponse;
import kr.hhplus.be.server.common.response.ApiResponse;

@Tag(name = "BestSeller", description = "BestSeller API")
public interface BestSellerApi {

    @Operation(summary = "최근 3일간 인기 상품 조회", description = "최근 3일간 인기 상품 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "최근 3일간 인기 상품이 조회됨",
            content = @Content(mediaType = "application/json")
    )
    ApiResponse<BestProductListResponse<BestProductResponse>> getBestProducts();
}
