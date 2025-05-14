package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.salesranking.SalesRankingRepository;
import kr.hhplus.be.server.support.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Component
public class BestSellerScheduler {

    private final ProductService productService;
    private final SalesRankingRepository salesRankingRepository;

    private static final String DAILY_SALES_PREFIX = "sales:daily:";
    private static final String LATEST_3DAYS_SALES_PREFIX = "sales:3days:";

    @CachePut(value = CacheNames.DAY3_BEST_SELLERS, key = "'best'", cacheManager = "redisCacheManager")
    @Scheduled(cron = "0 0/10 * * * *")
    public BestSellerDto getLatest3DaysTop5() {
        LocalDate today = LocalDate.now();
        String latest3daysSalesKey = LATEST_3DAYS_SALES_PREFIX + today.format(DateTimeFormatter.BASIC_ISO_DATE);

        List<String> keys = IntStream.rangeClosed(0, 2)
                .mapToObj(i -> DAILY_SALES_PREFIX + today.minusDays(i).format(DateTimeFormatter.BASIC_ISO_DATE))
                .toList();

        Long resultCount = salesRankingRepository.aggregateSales(keys, latest3daysSalesKey);
        salesRankingRepository.setSalesKeyTtl(latest3daysSalesKey, Duration.ofDays(1));

        log.info("[BestSellerScheduler] 최근 3일간 판매 상품 집계 완료. 총 상품 수 {}", resultCount);

        Set<ZSetOperations.TypedTuple<String>> top5 = salesRankingRepository.getTopProducts(latest3daysSalesKey, 5);

        if (top5 == null || top5.isEmpty()) {
            log.info("[BestSellerScheduler] 최근 3일간 인기 상품 집계 결과 없음");
            return BestSellerDto.empty();
        }

        List<Long> top5Ids = top5.stream()
                .map(t -> extractProductId(t.getValue()))
                .toList();

        Map<Long, Product> productMap = productService.getProductByIds(top5Ids);

        List<BestSeller> bestSellers = top5.stream()
                .map(t -> {
                    Long productId = extractProductId(t.getValue());
                    long sales = t.getScore() != null ? t.getScore().longValue() : 0L;
                    return BestSeller.of(productMap.get(productId), sales);
                })
                .toList();

        BestSellerDto result = BestSellerDto.of(bestSellers);
        log.info("[BestSellerScheduler] 최근 3일간 인기 상품 TOP5 집계 완료");
        return result;
    }

    private Long extractProductId(String value) {
        if (value == null || !value.startsWith("product:")) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return Long.valueOf(value.substring("product:".length()));
    }
}
