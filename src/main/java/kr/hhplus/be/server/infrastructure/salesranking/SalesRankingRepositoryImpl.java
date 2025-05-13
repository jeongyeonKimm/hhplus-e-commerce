package kr.hhplus.be.server.infrastructure.salesranking;

import kr.hhplus.be.server.domain.salesranking.SalesRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Map;

@RequiredArgsConstructor
@Repository
public class SalesRankingRepositoryImpl implements SalesRankingRepository {

    private final SalesRankingRedisRepository redisRepository;

    @Override
    public void increaseSalesCount(Map<Long, Long> productSales) {
        redisRepository.increaseSalesCount(productSales);
    }
}
