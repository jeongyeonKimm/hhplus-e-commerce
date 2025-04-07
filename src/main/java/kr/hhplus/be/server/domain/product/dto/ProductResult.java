package kr.hhplus.be.server.domain.product.dto;

import kr.hhplus.be.server.domain.product.Product;
import lombok.Builder;

public class ProductResult {

    private Long id;
    private String name;
    private Integer price;
    private Integer stock;

    @Builder
    private ProductResult(Long id, String name, Integer price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductResult from(Product product) {
        return ProductResult.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}
