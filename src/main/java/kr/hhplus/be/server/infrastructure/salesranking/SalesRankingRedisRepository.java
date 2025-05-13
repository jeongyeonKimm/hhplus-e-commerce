package kr.hhplus.be.server.infrastructure.salesranking;

import java.util.Map;

public interface SalesRankingRedisRepository {

    void increaseSalesCount(Map<Long, Long> productSales);
}
