package kr.hhplus.be.server.domain.coupon;

import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(Long couponId);

    List<Coupon> findAllById(List<Long> couponIds);

    Optional<Coupon> findByIdWithLock(Long id);

    boolean requestIssuance(Long userId, Long couponId);

    boolean isAlreadyIssued(Long userId, Long couponId);

    Set<ZSetOperations.TypedTuple<String>> getRequests(Long couponId, int batchSize);

    void deleteRequestKey(Long couponId);

    void addIssuedMember(Long couponId, List<Long> userIds);

    void addFailMember(Long couponId, List<Long> userIds);

    List<Coupon> findIssuableCoupons(LocalDate now);
}
