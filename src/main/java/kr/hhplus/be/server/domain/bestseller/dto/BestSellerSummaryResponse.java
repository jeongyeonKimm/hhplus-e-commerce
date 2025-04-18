package kr.hhplus.be.server.domain.bestseller.dto;

import lombok.Getter;

@Getter
public class BestSellerSummaryResponse {

    private Long productId;

    private String title;

    private String description;

    private Long price;

    private Long stock;

    private Long totalSales;

    public BestSellerSummaryResponse(Long productId, String title, String description, Long price, Long stock, Long totalSales) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.totalSales = totalSales;
    }
}
