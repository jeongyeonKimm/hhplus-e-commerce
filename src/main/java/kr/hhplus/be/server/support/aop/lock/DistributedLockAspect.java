package kr.hhplus.be.server.support.aop.lock;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.support.aop.AopForTransaction;
import kr.hhplus.be.server.support.aop.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String DISTRIBUTED_LOCK_KEY_PREFIX = "Lock:";
    private final LockStrategyFactory lockFactory;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(kr.hhplus.be.server.support.aop.lock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock lock = method.getAnnotation(DistributedLock.class);

        String key;
        try {
            key = DISTRIBUTED_LOCK_KEY_PREFIX + CustomSpringELParser.getDynamicValue(
                    signature.getParameterNames(), joinPoint.getArgs(), lock.key()
            );
            if (key == null || key.trim().isEmpty()) {
                log.error("DistributedLock AOP: SpEL 파싱 결과가 null 또는 비어 있음. keyExpression: {}", lock.key());
                throw new ApiException(ErrorCode.PARSING_ERROR);
            }
        } catch (Exception e) {
            log.error("DistributedLock AOP: SpEL 파싱 중 예외 발생 - keyExpression: {}", lock.key(), e);
            throw new ApiException(ErrorCode.PARSING_ERROR);
        }

        LockStrategy lockStrategy;
        try {
            lockStrategy = lockFactory.getLockStrategy(lock.type());
        } catch (Exception e) {
            log.error("DistributedLock AOP: LockStrategy 조회 실패 - type: {}", lock.type(), e);
            throw new ApiException(ErrorCode.LOCK_NOT_AVAILABLE);
        }

        try {
            return lockStrategy.execute(key, lock.timeUnit(), lock.waitTime(), lock.leaseTime(), () -> {
                try {
                    return aopForTransaction.proceed(joinPoint);
                } catch (Throwable e) {
                    log.error("DistributedLock AOP: 비즈니스 로직 실행 중 예외 발생", e);
                    throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            log.error("DistributedLock AOP: 분산락 수행 중 예외 발생 - key: {}", key, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
