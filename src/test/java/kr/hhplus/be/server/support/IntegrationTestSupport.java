package kr.hhplus.be.server.support;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
public abstract class IntegrationTestSupport {

    @Autowired
    private DbCleaner dbCleaner;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    public void setUp() {
        redisCacheManager.getCacheNames().forEach(name -> redisCacheManager.getCache(name).clear());
        redisTemplate.delete(redisTemplate.keys("*"));
        dbCleaner.execute();
    }

}
