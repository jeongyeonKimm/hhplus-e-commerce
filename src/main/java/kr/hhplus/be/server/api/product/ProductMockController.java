package kr.hhplus.be.server.api.product;

import kr.hhplus.be.server.api.product.dto.response.BestProductResponse;
import kr.hhplus.be.server.api.product.dto.response.ProductListResponse;
import kr.hhplus.be.server.api.product.dto.response.ProductResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductMockController {

    @GetMapping("/api/v1/products")
    public ApiResponse<ProductListResponse<ProductResponse>> getProducts() {
        List<ProductResponse> products = List.of(
                ProductResponse.builder()
                        .id(1L)
                        .name("iPhone13")
                        .price(1_000_000)
                        .stock(50)
                        .build(),
                ProductResponse.builder()
                        .id(1L)
                        .name("iPhone13")
                        .price(1_000_000)
                        .stock(50)
                        .build()
        );

        ProductListResponse<ProductResponse> result = ProductListResponse.<ProductResponse>builder()
                .products(products)
                .build();

        return ApiResponse.success(result);
    }

    @GetMapping("/api/v1/products/best")
    public ApiResponse<ProductListResponse<BestProductResponse>> getBestProducts() {
        List<BestProductResponse> bestProducts = List.of(
                BestProductResponse.builder()
                        .id(1L)
                        .name("iPhone13")
                        .price(1_000_000)
                        .stock(50)
                        .sales(200)
                        .build(),
                BestProductResponse.builder()
                        .id(1L)
                        .name("iPhone13")
                        .price(1_000_000)
                        .stock(50)
                        .sales(100)
                        .build()
        );

        ProductListResponse<BestProductResponse> result = ProductListResponse.<BestProductResponse>builder()
                .products(bestProducts)
                .build();

        return ApiResponse.success(result);
    }
}
