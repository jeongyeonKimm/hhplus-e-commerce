package kr.hhplus.be.server.interfaces.api.bestseller.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BestProductResponse {

    private Long id;
    private String name;
    private Integer price;
    private Integer stock;
    private Integer sales;

    @Builder
    private BestProductResponse(Long id, String name, Integer price, Integer stock, Integer sales) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.sales = sales;
    }
}
