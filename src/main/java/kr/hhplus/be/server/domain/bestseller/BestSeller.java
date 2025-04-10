package kr.hhplus.be.server.domain.bestseller;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BestSeller {

    private Long id;
    private Long productId;
    private String title;
    private byte[] description;
    private int price;
    private int stock;
    private Long sales;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BestSeller(Long id, Long productId, String title, byte[] description, int price, int stock, Long sales) {
        this.id = id;
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.sales = sales;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
