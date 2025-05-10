package kr.hhplus.be.server.support.aop.lock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class LettuceLockAopTest {

    @Autowired
    private TestService testService;

    @DisplayName("Lettuce 락 획득 요청이 동시에 100개 발생하면 모든 요청은 성공한다.")
    @Test
    void getLettuceLock_concurrently() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    String result = testService.testMethod(1L);
                    if (result.equals("success")) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        assertThat(successCount.get()).isEqualTo(threadCount);
    }

    @Configuration
    static class TestConfig {

        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    static class TestService {

        @LettuceLock(key = "'test:' + #id")
        public String testMethod(Long id) {
            return "success";
        }
    }
}
