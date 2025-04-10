package kr.hhplus.be.server.interfaces.api.bestseller.dto.response;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BestProductResponse {

    private Long id;
    private String name;
    private Integer price;
    private Integer stock;
    private Long sales;

    @Builder
    private BestProductResponse(Long id, String name, Integer price, Integer stock, Long sales) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.sales = sales;
    }

    public static BestProductResponse from(BestSeller bestSeller) {
        return new BestProductResponse(
                bestSeller.getId(),
                bestSeller.getTitle(),
                bestSeller.getPrice(),
                bestSeller.getStock(),
                bestSeller.getSales()
        );
    }
    }
