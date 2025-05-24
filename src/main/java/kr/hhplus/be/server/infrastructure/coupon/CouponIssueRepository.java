package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryReserveCoupon(Long userId, Long couponId) {
        String key = CouponKey.getCouponReserveKey(couponId);
        String value = "userId:" + userId;
        return Boolean.TRUE.equals(
                redisTemplate.opsForZSet().add(
                        key,
                        value,
                        System.currentTimeMillis()
                ));
    }

    public boolean isCouponIssued(Long userId, Long couponId) {
        String key = CouponKey.getCouponSuccessIssuanceKey(couponId);
        String value = "userId:" + userId;
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(key, value)
        );
    }

    public Set<ZSetOperations.TypedTuple<String>> getReservedUser(Long couponId, int size) {
        String key = CouponKey.getCouponReserveKey(couponId);
        return redisTemplate.opsForZSet()
                .rangeWithScores(key, 0, size - 1);
    }

    public void deleteSoldCoupon(Long couponId) {
        String key = CouponKey.getCouponReserveKey(couponId);
        redisTemplate.delete(key);
    }

    public void addIssuedUser(Long couponId, List<Long> userIds) {
        String key = CouponKey.getCouponSuccessIssuanceKey(couponId);
        userIds.forEach(userId -> {
            String value = "userId:" + userId;
            redisTemplate.opsForSet().add(key, value);
        });
    }

    public void addNotIssuedUser(Long couponId, List<Long> userIds) {
        String key = CouponKey.getCouponFailIssuanceKey(couponId);
        userIds.forEach(userId -> {
            String value = "userId:" + userId;
            redisTemplate.opsForSet().add(key, value);
        });
    }
}
