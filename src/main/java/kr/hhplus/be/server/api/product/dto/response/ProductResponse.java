package kr.hhplus.be.server.api.product.dto.response;

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
}
