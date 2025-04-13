package kr.hhplus.be.server.interfaces.api.product.dto.response;

import kr.hhplus.be.server.domain.product.dto.ProductResult;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private Integer price;
    private Integer stock;

    @Builder
    private ProductResponse(Long id, String name, Integer price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductResponse from(ProductResult productResult) {
        return ProductResponse.builder()
                .id(productResult.getId())
                .name(productResult.getName())
                .price(productResult.getPrice())
                .stock(productResult.getStock())
                .build();
    }
}
