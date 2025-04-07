package kr.hhplus.be.server.api.bestseller;

import kr.hhplus.be.server.api.bestseller.api.BestSellerApi;
import kr.hhplus.be.server.api.bestseller.dto.response.BestProductListResponse;
import kr.hhplus.be.server.api.bestseller.dto.response.BestProductResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BestSellerMockController implements BestSellerApi {

    @GetMapping("/api/v1/products/best")
    public ApiResponse<BestProductListResponse<BestProductResponse>> getBestProducts() {
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

        BestProductListResponse<BestProductResponse> result = BestProductListResponse.<BestProductResponse>builder()
                .products(bestProducts)
                .build();

        return ApiResponse.success(result);
    }
}
