package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.domain.salesranking.SalesRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BestSellerScheduler {

    private final SalesRankingService salesRankingService;

    @Scheduled(cron = "0 0/10 * * * *")
    public void scheduleBestSellerUpdate() {
        log.info("[BestSellerScheduler] 최근 3일간 인기 상품 TOP5 집계 시작");
        salesRankingService.getTop5BestSellersForThreeDays();
        log.info("[BestSellerScheduler] 최근 3일간 인기 상품 TOP5 집계 완료");
    }
}
