package kr.hhplus.be.server.application.bestseller.dto;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import lombok.Getter;

import java.util.List;

@Getter
public class BestSellerGetResult {

    private List<BestSeller> bestSellers;

    private BestSellerGetResult(List<BestSeller> bestSellers) {
        this.bestSellers = bestSellers;
    }

    public static BestSellerGetResult of(List<BestSeller> bestSellers) {
        return new BestSellerGetResult(bestSellers);
    }

    public static BestSellerGetResult from(List<BestSeller> bestSellers) {
        return new BestSellerGetResult(bestSellers);
    }
}
