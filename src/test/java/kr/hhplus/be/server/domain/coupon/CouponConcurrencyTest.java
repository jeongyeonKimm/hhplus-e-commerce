package kr.hhplus.be.server.domain.coupon;

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
class CouponConcurrencyTest extends IntegrationTestSupport {
    
    @Autowired
    private CouponService couponService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private CouponRepository couponRepository;
    
    @DisplayName("재고가 10개인 쿠폰을 사용자 20명이 동시에 발급 요청을 하면 10명은 쿠폰을 발급받지 못한다.")
    @Test
    void issueCoupon_concurrently() throws InterruptedException {
        Coupon coupon = Instancio.of(Coupon.class)
                .set(field(Coupon::getStock), 10)
                .create();
        Coupon savedCoupon = couponRepository.save(coupon);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1;
            executor.submit(() -> {
                try {
                    couponService.issueCoupon(userId, savedCoupon.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        assertThat(successCount.get()).isEqualTo(coupon.getStock());
        assertThat(failCount.get()).isEqualTo(10);
    }
}
