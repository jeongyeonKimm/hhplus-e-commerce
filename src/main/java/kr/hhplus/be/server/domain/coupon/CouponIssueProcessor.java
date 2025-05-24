package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.IssuanceResult;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponIssueProcessor {

    private static final int BATCH_SIZE = 1000;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public void processCouponIssuance() {
        LocalDate now = LocalDate.now();
        List<Coupon> coupons = couponRepository.findIssuableCoupons(now);

        for (Coupon coupon : coupons) {
            Set<ZSetOperations.TypedTuple<String>> requests = couponRepository.getRequests(coupon.getId(), BATCH_SIZE);
            if (requests == null || requests.isEmpty()) {
                couponRepository.deleteRequestKey(coupon.getId());
                continue;
            }

            IssuanceResult result = processRequests(coupon, requests);
            saveResults(coupon, result);
        }

        log.info("[CouponIssueProcessor] 쿠폰 발급 처리가 완료되었습니다.");
    }

    private IssuanceResult processRequests(Coupon coupon, Set<ZSetOperations.TypedTuple<String>> requests) {
        IssuanceResult result = new IssuanceResult();

        Long issuableCount = coupon.getStock();
        AtomicLong issueCount = new AtomicLong();

        for (ZSetOperations.TypedTuple<String> request : requests) {
            if (issuableCount <= issueCount.get()) {
                couponRepository.deleteRequestKey(coupon.getId());
                break;
            }

            Long userId = extractUserId(request);
            userRepository.findById(userId).ifPresent(user -> {
                try {
                    issueCount.getAndIncrement();
                    result.addSuccess(userId);
                } catch (Exception e) {
                    result.addFail(userId);
                    log.error("쿠폰 발급에 실패하였습니다. 실패 userId = {}", userId);
                }
            });
        }

        return result;
    }

    private void saveResults(Coupon coupon, IssuanceResult result) {
        if (!result.getSuccessUserIds().isEmpty()) {
            couponRepository.addIssuedMember(coupon.getId(), result.getSuccessUserIds());
        }

        if (!result.getFailUserIds().isEmpty()) {
            couponRepository.addFailMember(coupon.getId(), result.getFailUserIds());
        }

        coupon.deductCount(result.getSuccessUserIds().size());

        List<UserCoupon> userCoupons = result.getSuccessUserIds().stream()
                .map(userId -> UserCoupon.of(userId, coupon.getId()))
                .toList();

        userCouponRepository.saveAll(userCoupons);
    }

    private long extractUserId(ZSetOperations.TypedTuple<String> request) {
        return Long.parseLong(request.getValue().substring("userId:".length()));
    }
}
