package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
class PointConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @DisplayName("한명의 사용자가 동시에 여러번 충전 요청을 하면 모두 반영되어야 한다.")
    @Test
    void chargePoint_concurrently() throws InterruptedException {
        int threadCount = 10;
        long userId = 1L;
        long chargeAmount = 1000L;

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pointService.chargePoint(userId, chargeAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(failCount.get()).isEqualTo(0);

        Point finalPoint = pointService.getPoint(userId);
        assertThat(finalPoint.getBalance()).isEqualTo(threadCount * chargeAmount);
    }

    @DisplayName("한명의 사용자가 동시에 여러번 사용 요청을 하면 모두 반영되어야 한다.")
    @Test
    void usePoint_concurrently() throws InterruptedException {
        int threadCount = 10;
        long userId = 1L;
        long useAmount = 1000L;

        Point point = Instancio.of(Point.class)
                .set(field(Point::getUserId), userId)
                .set(field(Point::getBalance), 10000L)
                .create();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    pointService.usePoint(userId, useAmount);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(failCount.get()).isEqualTo(0);

        Point finalPoint = pointService.getPoint(userId);
        assertThat(finalPoint.getBalance()).isEqualTo(0);
    }
}
