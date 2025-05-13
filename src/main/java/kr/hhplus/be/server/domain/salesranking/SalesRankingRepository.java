package kr.hhplus.be.server.domain.salesranking;

import java.util.Map;

public interface SalesRankingRepository {

    void increaseSalesCount(Map<Long, Long> productSales);

}
