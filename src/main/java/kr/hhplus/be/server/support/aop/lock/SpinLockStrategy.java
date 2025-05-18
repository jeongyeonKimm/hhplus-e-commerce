package kr.hhplus.be.server.support.aop.lock;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static kr.hhplus.be.server.common.exception.ErrorCode.LOCK_INTERRUPTED;

@Slf4j
@RequiredArgsConstructor
@Component
public class SpinLockStrategy implements LockStrategy {

    private final SpinLockManager lockManager;

    @Override
    public <T> T execute(String key, TimeUnit timeUnit, long waitTime, long leaseTime, Supplier<T> supplier) {
        String value = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();

        try {
            while (!lockManager.tryLock(key, value, leaseTime)) {
                if (System.currentTimeMillis() - start > waitTime) {
                    log.info("Lock 획득 실패: {}", key);
                }
                Thread.sleep(100);
            }

            log.info("Lock 획득 성공: {}", key);

            return supplier.get();
        } catch (InterruptedException e) {
            log.error("Lock 대기 중 인터럽트 발생: {}", key, e);
            Thread.currentThread().interrupt();
            throw new ApiException(LOCK_INTERRUPTED);
        } finally {
            lockManager.unlock(key, value);
            log.info("Lock 해제: {}", key);
        }
    }

    @Override
    public LockType getLockType() {
        return LockType.SPIN_LOCK;
    }
}
