package kr.hhplus.be.server.support.aop.lock;

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
import java.util.UUID;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class LettuceLockAop {

    private static final String LETTUCE_LOCK_KEY_PREFIX = "Lock:";

    private final LettuceLockManager lockManager;

    @Around("@annotation(kr.hhplus.be.server.support.aop.lock.LettuceLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LettuceLock lettuceLock = method.getAnnotation(LettuceLock.class);

        String key = LETTUCE_LOCK_KEY_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), lettuceLock.key());
        String value = UUID.randomUUID().toString();
        long timeout = lettuceLock.timeout();

        while (!lockManager.tryLock(key, value, timeout)) {
            log.info("Lock 획득 실패: {}", key);
            Thread.sleep(100);
        }

        try {
            log.info("Lock 획득 성공: {}", key);
            return joinPoint.proceed();
        } finally {
            lockManager.unlock(key, value);
            log.info("Lock 해제: {}", key);
        }
    }
}
