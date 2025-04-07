package kr.hhplus.be.server.interfaces.api.product.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.api.product.dto.response.ProductListResponse;
import kr.hhplus.be.server.interfaces.api.product.dto.response.ProductResponse;
import kr.hhplus.be.server.common.response.ApiResponse;

@Tag(name = "Product", description = "Product API")
public interface ProductApi {

    @Operation(summary = "상품 목록 조회", description = "상품 목록 조회 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "상품이 조회됨",
            content = @Content(mediaType = "application/json")
    )
    ApiResponse<ProductListResponse<ProductResponse>> getProducts();
}
