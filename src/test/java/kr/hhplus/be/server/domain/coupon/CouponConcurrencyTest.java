package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.application.coupon.CouponFacade;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponConcurrencyTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("재고가 1개인 쿠폰을 사용자 2명이 동시에 발급 요청을 하면 1명은 쿠폰을 발급받지 못한다.")
    @Test
    void issueCoupon_concurrently() throws InterruptedException {
        long startTime = System.nanoTime();

        Coupon coupon = couponRepository.save(Coupon.of(
                "할인 쿠폰",
                1000L,
                DiscountType.AMOUNT,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                1L
        ));

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1;
            executor.submit(() -> {
                try {
                    couponService.issueCoupon(userId, coupon.getId());
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

        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000;

        System.out.println("실행 시간: " + durationMillis + " ms");
    }
}
