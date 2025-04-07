package kr.hhplus.be.server.interfaces.api.product.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductListResponse<T> {

    private List<T> products;

    @Builder
    private ProductListResponse(List<T> products) {
        this.products = products;
    }
}
