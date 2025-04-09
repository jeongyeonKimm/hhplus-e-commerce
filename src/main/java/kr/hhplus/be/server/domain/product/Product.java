package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.INSUFFICIENT_STOCK;

@Getter
public class Product {

    private Long id;
    private String name;
    private byte[] description;
    private Integer price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Product(Long id, String name, byte[] description, Integer price, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Product create(Long id, String name, String description, Integer price, Integer stock) {
        return Product.builder()
                .id(id)
                .name(name)
                .description(description.getBytes())
                .price(price)
                .stock(stock)
                .build();
    }

    public void deduct(int quantity) {
        if (quantity > stock) {
            throw new ApiException(INSUFFICIENT_STOCK);
        }

        this.stock -= quantity;
    }
}
