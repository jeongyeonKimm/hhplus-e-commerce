package kr.hhplus.be.server.support.aop.lock;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface LockStrategy {

    <T> T execute(String key, TimeUnit timeUnit, long waitTime, long leaseTime, Supplier<T> supplier);

    LockType getLockType();
}
