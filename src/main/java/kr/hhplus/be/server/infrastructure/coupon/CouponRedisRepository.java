package kr.hhplus.be.server.infrastructure.coupon;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Set;

public interface CouponRedisRepository {

    boolean addRequest(Long userId, Long couponId);

    boolean isMember(Long userId, Long couponId);

    Set<ZSetOperations.TypedTuple<String>> getRequests(Long couponId, int size);

    void deleteRequestKey(Long couponId);

    void addIssuedMember(Long couponId, List<Long> userIds);

    void addFailMember(Long couponId, List<Long> userIds);
}
