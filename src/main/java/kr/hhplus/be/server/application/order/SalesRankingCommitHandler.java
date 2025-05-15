package kr.hhplus.be.server.application.order;

import java.util.Map;

public interface SalesRankingCommitHandler {

    void handlerAfterOrderCommit(Map<Long, Long> productSales);
}
