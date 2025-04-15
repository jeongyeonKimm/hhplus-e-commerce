package kr.hhplus.be.server.interfaces.api.bestseller.dto.response;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BestProductResponse {

    private String name;
    private Long price;
    private Long stock;
    private Long sales;

    @Builder
    private BestProductResponse(String name, Long price, Long stock, Long sales) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.sales = sales;
    }

    public static BestProductResponse from(BestSeller bestSeller) {
        return new BestProductResponse(
                bestSeller.getTitle(),
                bestSeller.getPrice(),
                bestSeller.getStock(),
                bestSeller.getSales()
        );
    }
    }
