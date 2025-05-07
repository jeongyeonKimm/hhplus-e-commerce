package kr.hhplus.be.server.domain.bestseller;

import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BestSellerService {

    private final BestSellerRepository bestSellerRepository;

    @Cacheable(value = "bestSellers", key = "'best'", cacheManager = "redisCacheManager")
    public BestSellerDto getBestSellers() {
        log.info("🔥🔥🔥 캐시 없이 DB에서 인기 상품 조회 실행");
        List<BestSeller> bestSellers = bestSellerRepository.getBestSellers();
        return BestSellerDto.of(bestSellers);
    }

    public void save(BestSeller bestSeller) {
        bestSellerRepository.save(bestSeller);
    }

    public void deleteByCreatedAtBefore(LocalDateTime threshold) {
        bestSellerRepository.deleteByCreatedAtBefore(threshold);
    }
}
