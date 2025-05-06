package kr.hhplus.be.server.support.aop.lock;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.support.aop.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static kr.hhplus.be.server.common.exception.ErrorCode.LOCK_INTERRUPTED;
import static kr.hhplus.be.server.common.exception.ErrorCode.LOCK_NOT_AVAILABLE;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RedissonLockAop {

    private static final String REDISSON_LOCK_KEY_PREFIX = "Lock:";

    private final RedissonClient redissonClient;

    @Around("@annotation(kr.hhplus.be.server.support.aop.lock.RedissonLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock redissonLock = method.getAnnotation(RedissonLock.class);

        String key = REDISSON_LOCK_KEY_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), redissonLock.key());
        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), redissonLock.timeUnit());
            if (!available) {
                log.info("Lock 획득 실패: {}", key);
                throw new ApiException(LOCK_NOT_AVAILABLE);
            }

            log.info("Lock 획득 성공: {}", key);
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            log.error("Lock 대기 중 인터럽트 발생: {}", key, e);
            Thread.currentThread().interrupt();
            throw new ApiException(LOCK_INTERRUPTED);
        } finally {
            try {
                rLock.unlock();
                log.info("Lock 해제: {}", key);
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock이 이미 해제되었습니다.");
            }
        }
    }

}
