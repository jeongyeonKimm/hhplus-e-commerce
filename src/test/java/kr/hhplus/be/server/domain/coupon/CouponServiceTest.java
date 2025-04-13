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

        Coupon coupon = Coupon.of(
                couponId,
                "회원가입 할인 쿠폰",
                60_000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                0L
        );

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

        Coupon coupon = Coupon.of(
                couponId,
                "회원가입 할인 쿠폰",
                60_000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                10L
        );

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

        Coupon coupon = Coupon.of(
                couponId,
                "회원가입 할인 쿠폰",
                60_000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                10L
        );

        given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
        given(userCouponRepository.existByUserIdAndCouponId(userId, couponId)).willReturn(false);

        couponService.issueCoupon(userId, couponId);

        verify(couponRepository, times(1)).findById(couponId);
        verify(userCouponRepository, times(1)).existByUserIdAndCouponId(userId, couponId);
    }

}
