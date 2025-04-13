package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.ALREADY_USED_COUPON;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_COUPON;

@Getter
public class UserCoupon {

    private Long id;
    private Long userId;
    private Long couponId;
    private Boolean isUsed;
    private LocalDate issuedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Coupon coupon;

    private UserCoupon(Long id, Long userId, Long couponId) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.isUsed = false;
        this.issuedAt = LocalDate.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static UserCoupon of(Long id, Long userId, Long couponId) {
        return new UserCoupon(id, userId, couponId);
    }

    public Long getDiscountAmount(Long totalAmount) {
        if (!isAvailable()) {
            throw new ApiException(INVALID_COUPON);
        }

        return coupon.getDiscountAmount(totalAmount);
    }

    public void markUsed() throws ApiException {
        if (this.isUsed) {
            throw new ApiException(ALREADY_USED_COUPON);
        }

        this.isUsed = true;
    }

    public boolean isAvailable() {
        return !this.isUsed && !this.coupon.isExpired();
    }
}
