package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;

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
    private CouponRepository couponRepository;

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

        UserCoupon userCoupon = UserCoupon.of(
                userCouponId,
                100L,
                couponId,
                false,
                "할인 쿠폰",
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30)
        );

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        assertThatThrownBy(() -> couponService.redeemCoupon(userId, userCouponId))
                .isInstanceOf(ApiException.class)
                .hasMessage(COUPON_NOT_OWNED.getMessage());
    }

    @DisplayName("유효하지 않는 쿠폰을 조회하면 InvalidCouponException이 발생한다.")
    @Test
    void issueCoupon_throwInvalidCouponException_whenCouponNotExist() {
        long userId = 1L;
        long couponId = 2L;

        given(couponRepository.findById(couponId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.issueCoupon(userId, couponId))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_COUPON.getMessage());
    }

    @DisplayName("재고가 0 이하인 쿠폰을 발급 받으려는 경우 InsufficientCouponStockException이 발생한다.")
    @Test
    void issueCoupon_throwInsufficientCouponStockException_whenStockIsInsufficient() {
        long userId = 1L;
        long couponId = 2L;

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .title("회원가입 할인 쿠폰")
                .discountValue(60_000)
                .discountType(DiscountType.AMOUNT)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .stock(0)
                .build();

        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));

        assertThatThrownBy(() -> couponService.issueCoupon(userId, couponId))
                .isInstanceOf(ApiException.class)
                .hasMessage(INSUFFICIENT_COUPON_STOCK.getMessage());
    }

    @DisplayName("사용자가 이미 발급 받은 쿠폰이면 CouponAlreadyIssuedException이 발생한다.")
    @Test
    void issueCoupon_throwCouponAlreadyIssuedException_whenCouponIsAlreadyIssued() {
        long userId = 1L;
        long couponId = 2L;

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .title("회원가입 할인 쿠폰")
                .discountValue(60_000)
                .discountType(DiscountType.AMOUNT)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .stock(10)
                .build();

        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(userCouponRepository.existByUserIdAndCouponId(userId, couponId)).willReturn(true);

        assertThatThrownBy(() -> couponService.issueCoupon(userId, couponId))
                .isInstanceOf(ApiException.class)
                .hasMessage(COUPON_ALREADY_ISSUED.getMessage());
    }

    @DisplayName("남은 쿠폰 수량이 0 보다 많고, 사용자가 발급 받지 않은 쿠폰이면 쿠폰이 정상 발급 된다.")
    @Test
    void issueCoupon() {
        long userId = 1L;
        long couponId = 2L;

        Coupon coupon = Coupon.builder()
                .id(couponId)
                .title("회원가입 할인 쿠폰")
                .discountValue(60_000)
                .discountType(DiscountType.AMOUNT)
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2025, 4, 30))
                .stock(10)
                .build();

        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(userCouponRepository.existByUserIdAndCouponId(userId, couponId)).willReturn(false);

        couponService.issueCoupon(userId, couponId);

        verify(couponRepository, times(1)).findById(couponId);
        verify(userCouponRepository, times(1)).existByUserIdAndCouponId(userId, couponId);
    }

    @DisplayName("사용자가 보유 중인 쿠폰이면 쿠폰이 적용되고 사용 처리가 된다.")
    @Test
    void redeemCoupon() {
        long userCouponId = 1L;
        long userId = 2L;
        long couponId = 3L;

        UserCoupon userCoupon = UserCoupon.of(
                userCouponId,
                userId,
                couponId,
                false,
                "할인 쿠폰",
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30)
        );

        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.of(userCoupon));

        boolean isApplied = couponService.redeemCoupon(userId, userCouponId);

        assertThat(isApplied).isTrue();

        verify(userCouponRepository, times(1)).findById(userCouponId);
        verify(userCouponRepository, times(1)).save(userCoupon);
    }

}
