package kr.hhplus.be.server.support.aop.lock;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static kr.hhplus.be.server.common.exception.ErrorCode.LOCK_INTERRUPTED;
import static kr.hhplus.be.server.common.exception.ErrorCode.LOCK_NOT_AVAILABLE;

@Slf4j
@RequiredArgsConstructor
@Component
public class PubSubLockStrategy implements LockStrategy {

    private final RedissonClient redissonClient;

    @Override
    public <T> T execute(String key, TimeUnit timeUnit, long waitTime, long leaseTime, Supplier<T> supplier) {
        RLock rLock = redissonClient.getLock(key);
        boolean available = false;

        try {
            available = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if (!available) {
                log.info("Lock 획득 실패: {}", key);
                throw new ApiException(LOCK_NOT_AVAILABLE);
            }

            log.info("Lock 획득 성공: {}", key);

            return supplier.get();
        } catch (InterruptedException e) {
            log.error("Lock 대기 중 인터럽트 발생: {}", key, e);
            Thread.currentThread().interrupt();
            throw new ApiException(LOCK_INTERRUPTED);
        } finally {
            if (available && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    @Override
    public LockType getLockType() {
        return LockType.PUB_SUB_LOCK;
    }
}
