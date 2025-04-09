package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.ALREADY_USED_COUPON;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_COUPON_DATE;

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
        if (this.isUsed) {
            throw new ApiException(ALREADY_USED_COUPON);
        }

        if (this.expiredAt.isBefore(LocalDate.now()) ||
                this.issuedAt.isAfter(LocalDate.now())) {
            throw new ApiException(INVALID_COUPON_DATE);
        }

        this.isUsed = true;
    }
}
