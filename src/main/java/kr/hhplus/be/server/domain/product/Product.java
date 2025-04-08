package kr.hhplus.be.server.domain.product;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Product {

    private Long id;
    private String name;
    private byte[] description;
    private Integer price;
    private Integer stock;

    @Builder
    private Product(Long id, String name, byte[] description, Integer price, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public static Product create(Long id, String name, String description, Integer price, Integer stock) {
        return new Product(id, name, description.getBytes(), price, stock);
    }
}
