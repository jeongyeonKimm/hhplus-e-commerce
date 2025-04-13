package kr.hhplus.be.server.interfaces.api.product.dto.response;

import kr.hhplus.be.server.domain.product.dto.ProductResult;
import lombok.Getter;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private Long price;
    private Long stock;

    private ProductResponse(Long id, String name, Long price, Long stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductResponse from(ProductResult productResult) {
        return new ProductResponse(
                productResult.getId(),
                productResult.getName(),
                productResult.getPrice(),
                productResult.getStock()
        );
    }
}
