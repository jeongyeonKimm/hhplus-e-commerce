package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.application.bestseller.dto.BestSellerGetResult;
import kr.hhplus.be.server.domain.bestseller.BestSellerService;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BestSellerFacade {

    private final BestSellerService bestSellerService;

    public BestSellerGetResult getBestSellers() {
        BestSellerDto dto = bestSellerService.getBestSellers();
        return BestSellerGetResult.from(dto);
    }
}
