package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.salesranking.SalesRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class SalesRankingCommitHandlerImpl implements SalesRankingCommitHandler {

    private final SalesRankingRepository salesRankingRepository;

    @Override
    public void handlerAfterOrderCommit(Map<Long, Long> productSales) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    salesRankingRepository.increaseSalesCount(productSales);
                }
            });
        }
    }
}
