package kr.hhplus.be.server.infrastructure.salesranking;

import kr.hhplus.be.server.domain.salesranking.SalesRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class SalesRankingRepositoryImpl implements SalesRankingRepository {

    private final SalesRankingRedisRepository redisRepository;

    @Override
    public void increaseSalesCount(Map<Long, Long> productSales) {
        redisRepository.increaseSalesCount(productSales);
    }

    @Override
    public Long aggregateSales(List<String> keys, String salesKey) {
        return redisRepository.aggregateSales(keys, salesKey);
    }

    @Override
    public void setSalesKeyTtl(String key, Duration duration) {
        redisRepository.setSalesKeyTtl(key, duration);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getTopProducts(String key, int topN) {
        return redisRepository.getTopProducts(key, topN);
    }
}
