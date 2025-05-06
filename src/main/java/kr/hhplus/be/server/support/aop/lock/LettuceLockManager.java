package kr.hhplus.be.server.support.aop.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class LettuceLockManager {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(String key, String value, long timeoutMs) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(key, value, Duration.ofSeconds(timeoutMs));
    }

    public void unlock(String key, String value) {
        String lua = """
            if redis.call("get", KEYS[1]) == ARGV[1]
            then
                return redis.call("del", KEYS[1])
            else
                return 0
            end
        """;

        redisTemplate.execute(
                new DefaultRedisScript<>(lua, Long.class),
                Collections.singletonList(key),
                value
        );
    }

}
