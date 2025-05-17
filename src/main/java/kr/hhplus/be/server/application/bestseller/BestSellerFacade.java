package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSellerService;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import kr.hhplus.be.server.support.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BestSellerFacade {

    private final BestSellerService bestSellerService;

    @Cacheable(value = CacheNames.DAY3_BEST_SELLERS, key = "'best'", cacheManager = "redisCacheManager")
    public BestSellerDto getBestSellers() {
        return bestSellerService.getBestSellers();
    }
}
