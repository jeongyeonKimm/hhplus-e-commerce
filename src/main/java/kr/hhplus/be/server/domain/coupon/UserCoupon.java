package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.ALREADY_USED_COUPON;

public class UserCoupon {

    private Long id;
    private Long userId;
    private Long couponId;
    private Boolean isUsed;
    private String couponTitle;
    private LocalDate issuedAt;
    private LocalDate expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void redeem() {
        if (isUsed) {
            throw new ApiException(ALREADY_USED_COUPON);
        }

        this.isUsed = true;
    }
}
