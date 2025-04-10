package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.application.bestseller.dto.BestSellerGetResult;
import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.BestSellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BestSellerFacade {

    private final BestSellerService bestSellerService;

    public BestSellerGetResult getBestSellers() {
        List<BestSeller> bestSellers = bestSellerService.getBestSellers();
        return BestSellerGetResult.from(bestSellers);
    }
}
