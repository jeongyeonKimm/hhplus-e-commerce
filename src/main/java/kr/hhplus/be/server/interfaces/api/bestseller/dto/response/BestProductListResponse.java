package kr.hhplus.be.server.interfaces.api.bestseller.dto.response;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import lombok.Getter;

import java.util.List;

@Getter
public class BestProductListResponse<T> {

    private List<T> products;

    private BestProductListResponse(List<T> products) {
        this.products = products;
    }

    public static BestProductListResponse<BestProductResponse> from(BestSellerDto result) {
        List<BestSeller> bestSellers = result.getBestSellers();
        List<BestProductResponse> products = bestSellers.stream()
                .map(BestProductResponse::from)
                .toList();
        return new BestProductListResponse<BestProductResponse>(products);
    }
}
