package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorCode.COUPON_NOT_OWNED;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private UserCouponRepository userCouponRepository;

    @DisplayName("유저가 보유 중인 쿠폰이 아니면 CouponNotOwnedException이 발생한다.")
    @Test
    void redeemCoupon_throwCouponNotOwnedException_whenUserNotOwnedCoupon() {
        long userCouponId = 1L;
        given(userCouponRepository.findById(userCouponId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.redeemCoupon(userCouponId))
                .isInstanceOf(ApiException.class)
                .hasMessage(COUPON_NOT_OWNED.getMessage());
    }

}
