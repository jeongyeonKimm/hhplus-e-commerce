package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorCode.COUPON_NOT_OWNED;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_USER_COUPON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @DisplayName("사용자 쿠폰 ID로 쿠폰이 조회되지 않으면 InvalidUserCouponException이 발생한다.")
    @Test
    void redeemCoupon_throwInvalidUserCouponException_whenCouponNotExist() {
        long userCouponId = 1L;
        long userId = 2L;
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.redeemCoupon(userId, userCouponId))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_USER_COUPON.getMessage());
    }

    @DisplayName("유저가 보유 중인 쿠폰이 아니면 CouponNotOwnedException이 발생한다.")
    @Test
    void redeemCoupon_throwCouponNotOwnedException_whenUserNotOwnedCoupon() {
        long userCouponId = 1L;
        long userId = 2L;
        long couponId = 3L;

        UserCoupon userCoupon = UserCoupon.builder()
                .id(userCouponId)
                .userId(4L)
                .couponId(couponId)
                .isUsed(false)
                .couponTitle("할인 쿠폰")
                .issuedAt(LocalDate.of(2025, 4, 1))
                .expiredAt(LocalDate.of(2025, 4, 30))
                .build();

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        assertThatThrownBy(() -> couponService.redeemCoupon(userId, userCouponId))
                .isInstanceOf(ApiException.class)
                .hasMessage(COUPON_NOT_OWNED.getMessage());
    }

    @DisplayName("사용자가 보유 중인 쿠폰이면 쿠폰이 적용되고 사용 처리가 된다.")
    @Test
    void redeemCoupon() {
        long userCouponId = 1L;
        long userId = 2L;
        long couponId = 3L;

        UserCoupon userCoupon = UserCoupon.builder()
                .id(userCouponId)
                .userId(userId)
                .couponId(couponId)
                .isUsed(false)
                .couponTitle("할인 쿠폰")
                .issuedAt(LocalDate.of(2025, 4, 1))
                .expiredAt(LocalDate.of(2025, 4, 30))
                .build();

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        boolean isApplied = couponService.redeemCoupon(userId, userCouponId);

        assertThat(isApplied).isTrue();

        verify(userCouponRepository, times(1)).findById(userCouponId);
        verify(userCouponRepository, times(1)).save(userCoupon);
    }

}
