package kr.hhplus.be.server.domain.salesranking;

import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SalesRankingRepository {

    void increaseSalesCount(Map<Long, Long> productSales);

    Long aggregateSales(List<String> keys, String salesKey);

    void setSalesKeyTtl(String key, Duration duration);

    Set<ZSetOperations.TypedTuple<String>> getTopProducts(String key, int topN);
}
