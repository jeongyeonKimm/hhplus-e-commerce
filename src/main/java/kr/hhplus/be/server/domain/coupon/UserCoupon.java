package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;

@Getter
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

    private UserCoupon(Long id, Long userId, Long couponId, Boolean isUsed, String couponTitle, LocalDate issuedAt, LocalDate expiredAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.isUsed = isUsed;
        this.couponTitle = couponTitle;
        this.issuedAt = issuedAt;
        this.expiredAt = expiredAt;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static UserCoupon of(long id, long userId, long couponId, boolean isUsed, String couponTitle, LocalDate issuedAt, LocalDate expiredAt) {
        return new UserCoupon(id, userId, couponId, isUsed, couponTitle, issuedAt, expiredAt);
    }

    public void redeem() {
        if (this.isUsed) {
            throw new ApiException(ALREADY_USED_COUPON);
        }

        if (this.expiredAt.isBefore(LocalDate.now())) {
            throw new ApiException(COUPON_DATE_EXPIRED);
        }

        this.isUsed = true;
    }
}
