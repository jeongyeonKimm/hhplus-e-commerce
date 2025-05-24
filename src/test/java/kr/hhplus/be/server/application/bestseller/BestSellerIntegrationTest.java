package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.salesranking.SalesRankingKey;
import kr.hhplus.be.server.domain.salesranking.SalesRankingService;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import kr.hhplus.be.server.support.cache.CacheNames;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

public class BestSellerIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BestSellerScheduler bestSellerScheduler;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SalesRankingService salesRankingService;

    private static final String CACHE_KEY = "best";

    @DisplayName("최근 3일 판매량 집계 및 상위 5개 인기 상품을 캐싱한다.")
    @Test
    void getLatest3DaysTop5_aggregate_and_caching() {
        LongStream.rangeClosed(1, 5)
                .mapToObj(i -> {
                    Product product = Product.of(
                            "Product " + i,
                            "상품입니다.",
                            1000L * i,
                            10L * i
                    );
                    productRepository.save(product);
                    return product;
                })
                .toList();

        List<Product> savedProducts = productRepository.findAll();
        List<Long> productIds = savedProducts.stream()
                .map(Product::getId)
                .toList();

        LocalDate today = LocalDate.now();
        addSales(today, productIds.get(0), 10);
        addSales(today.minusDays(1), productIds.get(1), 20);
        addSales(today.minusDays(2), productIds.get(2), 15);
        addSales(today.minusDays(2), productIds.get(3), 5);
        addSales(today.minusDays(2), productIds.get(4), 1);

        bestSellerScheduler.scheduleBestSellerUpdate();

        Cache cache = cacheManager.getCache(CacheNames.DAY3_BEST_SELLERS);
        assertThat(cache).isNotNull();

        BestSellerDto cached = cache.get(CACHE_KEY, BestSellerDto.class);
        assertThat(cached).isNotNull();
        assertThat(cached.getBestSellers()).hasSize(5);

        List<Long> expectedIds = List.of(
                productIds.get(1),
                productIds.get(2),
                productIds.get(0),
                productIds.get(3),
                productIds.get(4)
        );
        List<Long> actualIds = cached.getBestSellers().stream()
                .map(BestSeller::getProductId)
                .toList();

        assertThat(actualIds).isEqualTo(expectedIds);
    }

    private void addSales(LocalDate date, Long productId, long score) {
        String key = SalesRankingKey.getSalesDailyKey(date);
        String value = "product:" + productId.toString();
        redisTemplate.opsForZSet().add(key, value, score);
    }
}
