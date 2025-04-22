package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class PointConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @DisplayName("한 명의 사용자가 동시에 여러 번 충전 요청을 하면 한 번만 반영되어야 한다.")
    @Test
    void chargePoint_concurrently() throws InterruptedException {
        long startTime = System.nanoTime();

        int threadCount = 2;
        long userId = 1L;
        long chargeAmount = 1000L;
        long initBalance = 10000L;

        pointRepository.savePoint(Point.of(userId, initBalance));

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

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Point finalPoint = pointService.getPoint(userId);
        long expectedBalance = initBalance + chargeAmount;
        assertThat(finalPoint.getBalance()).isEqualTo(expectedBalance);

        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000;

        System.out.println("실행 시간: " + durationMillis + " ms");
    }

    @DisplayName("한 명의 사용자가 동시에 여러 번 사용 요청을 하면 한 번만 반영되어야 한다.")
    @Test
    void usePoint_concurrently() throws InterruptedException {
        long startTime = System.nanoTime();

        int threadCount = 2;
        long userId = 1L;
        long useAmount = 1000L;
        long initBalance = 1000L;

        pointRepository.savePoint(Point.of(userId, initBalance));

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

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        Point finalPoint = pointService.getPoint(userId);
        assertThat(finalPoint.getBalance()).isEqualTo(0);

        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000;

        System.out.println("실행 시간: " + durationMillis + " ms");
    }
}
