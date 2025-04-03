package kr.hhplus.be.server.api.product.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.api.product.dto.response.BestProductResponse;
import kr.hhplus.be.server.api.product.dto.response.ProductListResponse;
import kr.hhplus.be.server.api.product.dto.response.ProductResponse;
import kr.hhplus.be.server.common.response.ApiResponse;

@Tag(name = "Product", description = "Product API")
public interface ProductApi {

    @Operation(summary = "상품 목록 조회", description = "상품 목록 조회 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "상품이 조회됨",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "상품이 조회됨",
                                    summary = "상품이 조회됨",
                                    value = """
                                            {
                                              "code": 200,
                                              "message": "요청이 정상적으로 처리되었습니다.",
                                              "data": {
                                                "products": [
                                                  {
                                                    "id": 1,
                                                    "name": "Macbook Pro",
                                                    "price": 2000000,
                                                    "stock": 10
                                                  },
                                                  {
                                                    "id": 2,
                                                    "name": "iPhone 12",
                                                    "price": 1200000,
                                                    "stock": 20
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    })
    )
    ApiResponse<ProductListResponse<ProductResponse>> getProducts();

    @Operation(summary = "최근 3일간 인기 상품 조회", description = "최근 3일간 인기 상품 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "최근 3일간 인기 상품이 조회됨",
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "최근 3일간 인기 상품이 조회됨",
                                    summary = "최근 3일간 인기 상품이 조회됨",
                                    value = """
                                            {
                                               "code": 200,
                                               "message": "요청이 정상적으로 처리되었습니다.",
                                               "data": [
                                                 {
                                                   "id": 1,
                                                   "name": "ice americano",
                                                   "price": 1000,
                                                   "sales": 100,
                                                   "stock": 100
                                                 },
                                                 {
                                                   "id": 2,
                                                   "name": "iPhone 12",
                                                   "price": 1200000,
                                                   "sales": 90,
                                                   "stock": 100
                                                 }
                                               ]
                                             }
                                            """
                            )
                    })
    )
    ApiResponse<ProductListResponse<BestProductResponse>> getBestProducts();
}
