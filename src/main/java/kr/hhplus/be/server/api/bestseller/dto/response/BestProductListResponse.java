package kr.hhplus.be.server.api.bestseller.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class BestProductListResponse<T> {

    private List<T> products;

    @Builder
    private BestProductListResponse(List<T> products) {
        this.products = products;
    }
}
