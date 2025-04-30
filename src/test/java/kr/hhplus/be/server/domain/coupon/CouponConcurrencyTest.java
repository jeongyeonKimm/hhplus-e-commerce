package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.support.IntegrationTestSupport;
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
class CouponConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @DisplayName("재고가 50개인 쿠폰을 사용자 100명이 동시에 발급 요청을 하면 50명은 쿠폰 발급에 성공하고 50명은 쿠폰을 발급받지 못한다.")
    @Test
    void issueCoupon_concurrently() throws InterruptedException {
        long startTime = System.nanoTime();

        Coupon coupon = couponRepository.save(Coupon.of(
                "할인 쿠폰",
                1000L,
                DiscountType.AMOUNT,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                50L
        ));

        int threadCount = 100;
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

        assertThat(successCount.get()).isEqualTo(50);
        assertThat(failCount.get()).isEqualTo(50);

        Coupon issuedCoupon = couponRepository.findById(coupon.getId()).orElseThrow();
        assertThat(issuedCoupon.getStock()).isEqualTo(0L);

        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000;

        System.out.println("실행 시간: " + durationMillis + " ms");
    }
}
