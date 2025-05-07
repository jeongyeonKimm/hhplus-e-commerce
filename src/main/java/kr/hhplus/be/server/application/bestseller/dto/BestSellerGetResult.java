package kr.hhplus.be.server.application.bestseller.dto;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BestSellerGetResult {

    private List<BestSeller> bestSellers;

    private BestSellerGetResult(List<BestSeller> bestSellers) {
        this.bestSellers = bestSellers;
    }

    public static BestSellerGetResult from(BestSellerDto bestSeller) {
        return new BestSellerGetResult(bestSeller.getBestSellers());
    }
}
