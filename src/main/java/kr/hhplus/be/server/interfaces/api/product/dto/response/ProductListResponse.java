package kr.hhplus.be.server.interfaces.api.product.dto.response;

import kr.hhplus.be.server.domain.product.dto.ProductResult;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProductListResponse<T> {

    private List<T> products;

    @Builder
    private ProductListResponse(List<T> products) {
        this.products = products;
    }

    public static ProductListResponse<ProductResponse> from(List<ProductResult> results) {
        List<ProductResponse> products = results.stream()
                .map(ProductResponse::from)
                .toList();

        return ProductListResponse.<ProductResponse>builder()
                .products(products)
                .build();
    }
}
