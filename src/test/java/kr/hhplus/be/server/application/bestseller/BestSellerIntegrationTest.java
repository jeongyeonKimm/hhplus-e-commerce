package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.salesranking.SalesRankingKey;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import kr.hhplus.be.server.support.cache.CacheNames;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class BestSellerIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BestSellerScheduler bestSellerScheduler;

    @Autowired
    private CacheManager cacheManager;

    @MockitoBean
    private ProductService productService;

    private static final String CACHE_KEY = "best";

    @DisplayName("최근 3일 판매량 집계 및 상위 5개 인기 상품을 캐싱한다.")
    @Test
    void getLatest3DaysTop5_aggregate_and_caching() {
        LocalDate today = LocalDate.now();
        addSales(today, 101L, 10);
        addSales(today.minusDays(1), 102L, 20);
        addSales(today.minusDays(2), 103L, 15);
        addSales(today.minusDays(2), 104L, 5);
        addSales(today.minusDays(2), 105L, 1);

        Map<Long, Product> productMap = LongStream.rangeClosed(1, 5)
                .boxed()
                .collect(Collectors.toMap(
                        i -> 100L + i,
                        i -> Instancio.of(Product.class)
                                .set(field(Product::getId), 100L + i)
                                .set(field(Product::getName), "Product " + i)
                                .set(field(Product::getDescription), "상품입니다.")
                                .set(field(Product::getPrice), 1000L * i)
                                .set(field(Product::getStock), 10L * i)
                                .create()
                ));

        when(productService.getProductByIds(anyList())).thenReturn(productMap);

        bestSellerScheduler.scheduleBestSellerUpdate();

        Cache cache = cacheManager.getCache(CacheNames.DAY3_BEST_SELLERS);
        assertThat(cache).isNotNull();

        BestSellerDto cached = cache.get(CACHE_KEY, BestSellerDto.class);
        assertThat(cached).isNotNull();
        assertThat(cached.getBestSellers()).hasSize(5);

        List<Long> expectedIds = List.of(102L, 103L, 101L, 104L, 105L);
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
