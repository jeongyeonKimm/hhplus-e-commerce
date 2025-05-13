package kr.hhplus.be.server.infrastructure.salesranking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class SalesRankingRedisRepositoryImpl implements SalesRankingRedisRepository {

    private static final String SALES_DAILY_KEY_PREFIX = "sales:daily:";
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void increaseSalesCount(Map<Long, Long> productSales) {
        String key = generateDailySalesKey();
        for (Map.Entry<Long, Long> entry : productSales.entrySet()) {
            String member = "product:" + entry.getKey();
            redisTemplate.opsForZSet().incrementScore(key, member, entry.getValue());
        }
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    private String generateDailySalesKey() {
        LocalDate today = LocalDate.now();
        return SALES_DAILY_KEY_PREFIX + today.format(DateTimeFormatter.BASIC_ISO_DATE);
    }
}
