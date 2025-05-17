package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponRedisRepository couponRedisRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Override
    public List<Coupon> findAllById(List<Long> couponIds) {
        return couponJpaRepository.findAllById(couponIds);
    }

    @Override
    public Optional<Coupon> findByIdWithLock(Long id) {
        return couponJpaRepository.findByIdWithLock(id);
    }

    @Override
    public boolean requestIssuance(Long userId, Long couponId) {
        return couponRedisRepository.addRequest(userId, couponId);
    }

    @Override
    public boolean isAlreadyIssued(Long userId, Long couponId) {
        return couponRedisRepository.isMember(userId, couponId);
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> getRequests(Long couponId, int size) {
        return couponRedisRepository.getRequests(couponId, size);
    }

    @Override
    public void deleteRequestKey(Long couponId) {
        couponRedisRepository.deleteRequestKey(couponId);
    }

    @Override
    public void addIssuedMember(Long couponId, List<Long> userIds) {
        couponRedisRepository.addIssuedMember(couponId, userIds);
    }

    @Override
    public void addFailMember(Long couponId, List<Long> userIds) {
        couponRedisRepository.addFailMember(couponId, userIds);
    }

    @Override
    public List<Coupon> findIssuableCoupons(LocalDate now) {
        return couponJpaRepository.findIssuableCoupons(now);
    }
}
