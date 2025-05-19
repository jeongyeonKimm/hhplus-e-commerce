package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CouponIssueRepositoryTest extends IntegrationTestSupport {

    private static final String REQUEST_ISSUE_COUPON_KEY = "coupon:request:%d";
    private static final String ISSUED_COUPON_KEY = "coupon:issued:%d";
    private static final String FAIL_COUPON_KEY = "coupon:fail:%d";

    @Autowired
    private CouponIssueRepository couponIssueRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DisplayName("쿠폰 발급 요청이 오면 ZSET에 현재 시간을 score로, userId를 value로 저장한다.")
    @Test
    void addRequest() {
        Long couponId = 1L;

        couponIssueRepository.tryReserveCoupon(10L, couponId);
        couponIssueRepository.tryReserveCoupon(11L, couponId);

        String key = String.format(REQUEST_ISSUE_COUPON_KEY, couponId);
        List<Long> values = redisTemplate.opsForZSet()
                .range(key, 0, 1)
                .stream()
                .map(userId -> Long.parseLong(userId.substring("userId:".length())))
                .toList();

        assertThat(values).containsExactly(10L, 11L);
    }

    @DisplayName("userId가 SET에 존재하지는 여부를 확인한다.")
    @Test
    void isMember() {
        Long couponId = 1L;
        Long userId = 10L;
        String key = String.format(ISSUED_COUPON_KEY, couponId);
        String value = "userId:" + userId;

        redisTemplate.opsForSet().add(key, value);

        boolean isMember = couponIssueRepository.isCouponIssued(userId, couponId);

        assertThat(isMember).isTrue();
    }

    @DisplayName("ZSET에 있는 value를 size만큼 조회할 수 있다.")
    @Test
    void getRequests() {
        Long couponId = 1L;
        String key = String.format(REQUEST_ISSUE_COUPON_KEY, couponId);
        redisTemplate.opsForZSet().add(key, "userId:" + 10L, System.currentTimeMillis());
        redisTemplate.opsForZSet().add(key, "userId:" + 20L, System.currentTimeMillis());
        redisTemplate.opsForZSet().add(key, "userId:" + 30L, System.currentTimeMillis());

        int size = 2;
        List<Long> requestUserIds = couponIssueRepository.getReservedUser(couponId, size)
                .stream()
                .map(request -> Long.parseLong(request.getValue().substring("userId:".length())))
                .toList();

        assertThat(requestUserIds).containsExactly(10L, 20L);
    }

    @DisplayName("ZSET에 있는 key를 삭제한다.")
    @Test
    void deleteRequestKey() {
        Long couponId = 1L;
        String key = String.format(REQUEST_ISSUE_COUPON_KEY, couponId);
        redisTemplate.opsForZSet().add(key, "userId:" + 10L, System.currentTimeMillis());

        couponIssueRepository.deleteSoldCoupon(couponId);

        Long size = redisTemplate.opsForZSet().size(key);

        assertThat(size).isZero();
    }

    @DisplayName("쿠폰이 발급된 사용자 아이디를 coupon:issued:{couponId}를 키로 가지는 SET에 저장한다.")
    @Test
    void addIssuedMember() {
        Long couponId = 1L;
        List<Long> userIds = List.of(10L, 20L, 30L);
        String key = String.format(ISSUED_COUPON_KEY, couponId);

        couponIssueRepository.addIssuedUser(couponId, userIds);

        List<Long> issuedUserIds = redisTemplate.opsForSet().members(key)
                .stream()
                .map(value -> Long.parseLong(value.substring("userId:".length())))
                .toList();

        assertThat(issuedUserIds).containsExactly(10L, 20L, 30L);
    }

    @DisplayName("쿠폰에 실패한 사용자 아이디를 coupon:fail:{couponId}를 키로 가지는 SET에 저장한다.")
    @Test
    void addFailMember() {
        Long couponId = 1L;
        List<Long> userIds = List.of(10L, 20L, 30L);
        String key = String.format(FAIL_COUPON_KEY, couponId);

        couponIssueRepository.addNotIssuedUser(couponId, userIds);

        List<Long> issuedUserIds = redisTemplate.opsForSet().members(key)
                .stream()
                .map(value -> Long.parseLong(value.substring("userId:".length())))
                .toList();

        assertThat(issuedUserIds).containsExactly(10L, 20L, 30L);
    }
}
