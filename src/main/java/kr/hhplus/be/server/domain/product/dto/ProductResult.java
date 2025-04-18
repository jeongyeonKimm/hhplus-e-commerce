package kr.hhplus.be.server.domain.product.dto;

import kr.hhplus.be.server.domain.product.Product;
import lombok.Getter;

@Getter
public class ProductResult {

    private Long id;
    private String name;
    private Long price;
    private Long stock;

    private ProductResult(Long id, String name, Long price, Long stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductResult from(Product product) {
        return new ProductResult(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }
}
