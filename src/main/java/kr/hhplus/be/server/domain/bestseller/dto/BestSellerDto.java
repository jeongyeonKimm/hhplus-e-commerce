package kr.hhplus.be.server.domain.bestseller.dto;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BestSellerDto {

    private List<BestSeller> bestSellers;

    private BestSellerDto(List<BestSeller> bestSellers) {
        this.bestSellers = bestSellers;
    }

    public static BestSellerDto of(List<BestSeller> bestSellers) {
        return new BestSellerDto(bestSellers);
    }

    public static BestSellerDto empty() {
        return new BestSellerDto(Collections.emptyList());
    }
}
