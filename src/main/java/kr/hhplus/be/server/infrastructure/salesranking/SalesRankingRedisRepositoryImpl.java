package kr.hhplus.be.server.infrastructure.salesranking;

import kr.hhplus.be.server.domain.salesranking.SalesRankingKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class SalesRankingRedisRepositoryImpl implements SalesRankingRedisRepository {

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

    @Override
    public Long aggregateSales(List<String> keys, String salesKey) {
        return redisTemplate.opsForZSet()
                .unionAndStore(
                        keys.get(0),
                        keys.subList(1, keys.size()),
                        salesKey
                );
    }

    @Override
    public void setSalesKeyTtl(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getTopProducts(String key, int topN) {
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, topN - 1);
    }

    private String generateDailySalesKey() {
        LocalDate today = LocalDate.now();
        return SalesRankingKey.getSalesDailyKey(today);
    }
}
