package kr.hhplus.be.server.infrastructure.salesranking;

import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class SalesRankingRedisRepositoryImplTest extends IntegrationTestSupport {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SalesRankingRedisRepository redisRepository;

    private static final String DAILY_SALES_KEY_PREFIX = "sales:daily:";
    
    @DisplayName("상품 판매량을 ZSET에 증가시킨다.")
    @Test
    void increaseSalesCount() {
        Map<Long, Long> sales = new HashMap<>();
        sales.put(101L, 3L);
        sales.put(102L, 5L);

        redisRepository.increaseSalesCount(sales);

        String key = DAILY_SALES_KEY_PREFIX + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Double score1 = redisTemplate.opsForZSet().score(key, "product:101");
        Double score2 = redisTemplate.opsForZSet().score(key, "product:102");

        assertThat(score1).isEqualTo(3);
        assertThat(score2).isEqualTo(5);
    }

    @DisplayName("상품 판매량이 ZSET에 등록되면 TTL이 30일로 설정된다.")
    @Test
    void setTtl_30days() {
        Map<Long, Long> sales = Map.of(201L, 1L);

        redisRepository.increaseSalesCount(sales);

        String key = DAILY_SALES_KEY_PREFIX + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Long ttl = redisTemplate.getExpire(key, TimeUnit.DAYS);

        assertThat(ttl).isGreaterThan(0L);
        assertThat(ttl).isLessThanOrEqualTo(30L);
    }
}
