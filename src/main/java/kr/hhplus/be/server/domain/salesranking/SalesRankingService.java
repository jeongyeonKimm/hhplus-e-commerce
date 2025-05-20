package kr.hhplus.be.server.domain.salesranking;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.salesranking.dto.SalesRankingResult;
import kr.hhplus.be.server.support.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SalesRankingService {

    private final ProductService productService;
    private final SalesRankingRepository salesRankingRepository;

    @CachePut(value = CacheNames.DAY3_BEST_SELLERS, key = "'best'", cacheManager = "redisCacheManager")
    public BestSellerDto getTop5BestSellersForThreeDays() {
        LocalDate today = LocalDate.now();
        SalesPeriod period = SalesPeriod.lastThreeDays(today);

        SalesRankingResult ranking = calculateSalesRanking(period);
        if (ranking.isEmpty()) {
            return BestSellerDto.empty();
        }

        return createBestSellerDto(ranking.getTopTuples());
    }

    private SalesRankingResult calculateSalesRanking(SalesPeriod period) {
        List<String> dailyKeys = period.getDailyKeys();
        String aggregatedKey = period.getAggregatedKey();

        salesRankingRepository.aggregateSales(dailyKeys, aggregatedKey);
        salesRankingRepository.setSalesKeyTtl(aggregatedKey, Duration.ofDays(1));

        Set<ZSetOperations.TypedTuple<String>> top5 = salesRankingRepository.getTopProducts(aggregatedKey, 5);

        return SalesRankingResult.from(top5);
    }

    private BestSellerDto createBestSellerDto(Set<ZSetOperations.TypedTuple<String>> topTuples) {
        List<Long> productIds = topTuples.stream()
                .map(t -> SalesRankingResult.extractProductId(t.getValue()))
                .toList();

        Map<Long, Product> productMap = productService.getProductByIds(productIds);

        List<BestSeller> bestSellers = topTuples.stream()
                .map(t -> {
                    Long productId = SalesRankingResult.extractProductId(t.getValue());
                    long sales = Optional.ofNullable(t.getScore()).orElse(0.0).longValue();
                    return BestSeller.of(productMap.get(productId), sales);
                })
                .toList();

        return BestSellerDto.of(bestSellers);
    }
}
