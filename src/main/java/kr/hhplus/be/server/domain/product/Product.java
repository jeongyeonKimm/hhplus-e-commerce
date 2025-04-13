package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.Getter;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.INSUFFICIENT_STOCK;

@Getter
public class Product {

    private Long id;
    private String name;
    private byte[] description;
    private Long price;
    private Long stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Product(Long id, String name, byte[] description, Long price, Long stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Product of(Long id, String name, byte[] description, Long price, Long stock) {
        return new Product(id, name, description, price, stock);
    }

    public void deduct(Long quantity) {
        if (quantity > stock) {
            throw new ApiException(INSUFFICIENT_STOCK);
        }

        this.stock -= quantity;
    }
}
