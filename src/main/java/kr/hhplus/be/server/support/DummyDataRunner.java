package kr.hhplus.be.server.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DummyDataRunner implements ApplicationRunner {

    private final DummyDataService dummyDataService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("🔥 DummyDataService started");

        try {
            dummyDataService.batchInsertUsersWithMultiThread();
            dummyDataService.batchInsertPointsWithMultiThread();
            dummyDataService.batchInsertPointHistoriesWithMultiThread();
            dummyDataService.batchInsertProductsWithMultiThread();
            dummyDataService.batchInsertCouponsWithMultiThread();
            dummyDataService.batchInsertUserCouponsWithMultiThread();
            dummyDataService.batchInsertOrdersWithMultiThread();
            dummyDataService.batchInsertOrderProductsWithMultiThread();
            dummyDataService.batchInsertBestSellersWithMultiThread();
        } catch (Exception e) {
            log.error("❌ 더미 데이터 생성 중 오류 발생", e);
        }

        log.info("✅ DummyDataService finished");
    }
}
