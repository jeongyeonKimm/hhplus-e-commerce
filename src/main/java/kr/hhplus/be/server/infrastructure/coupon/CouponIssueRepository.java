package kr.hhplus.be.server.infrastructure.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {

    private static final String REQUEST_ISSUE_COUPON_KEY = "coupon:request:%d";
    private static final String ISSUED_COUPON_KEY = "coupon:issued:%d";
    private static final String FAIL_COUPON_KEY = "coupon:fail:%d";

    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryReserveCoupon(Long userId, Long couponId) {
        String key = String.format(REQUEST_ISSUE_COUPON_KEY, couponId);
        String value = "userId:" + userId;
        return Boolean.TRUE.equals(
                redisTemplate.opsForZSet().add(
                        key,
                        value,
                        System.currentTimeMillis()
                ));
    }

    public boolean isCouponIssued(Long userId, Long couponId) {
        String key = String.format(ISSUED_COUPON_KEY, couponId);
        String value = "userId:" + userId;
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(key, value)
        );
    }

    public Set<ZSetOperations.TypedTuple<String>> getReservedUser(Long couponId, int size) {
        String key = String.format(REQUEST_ISSUE_COUPON_KEY, couponId);
        return redisTemplate.opsForZSet()
                .rangeWithScores(key, 0, size - 1);
    }

    public void deleteSoldCoupon(Long couponId) {
        String key = String.format(REQUEST_ISSUE_COUPON_KEY, couponId);
        redisTemplate.delete(key);
    }

    public void addIssuedUser(Long couponId, List<Long> userIds) {
        String key = String.format(ISSUED_COUPON_KEY, couponId);
        userIds.forEach(userId -> {
            String value = "userId:" + userId;
            redisTemplate.opsForSet().add(key, value);
        });
    }

    public void addNotIssuedUser(Long couponId, List<Long> userIds) {
        String key = String.format(FAIL_COUPON_KEY, couponId);
        userIds.forEach(userId -> {
            String value = "userId:" + userId;
            redisTemplate.opsForSet().add(key, value);
        });
    }
}
