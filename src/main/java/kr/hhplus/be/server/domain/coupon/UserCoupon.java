package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static kr.hhplus.be.server.common.exception.ErrorCode.ALREADY_USED_COUPON;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_COUPON;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_coupon")
@Entity
public class UserCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long couponId;

    private Boolean isUsed;

    private LocalDate issuedAt;

    @Transient
    private Coupon coupon;

    private UserCoupon(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
        this.isUsed = false;
        this.issuedAt = LocalDate.now();
    }

    private UserCoupon(User user, Coupon coupon) {
        this.userId = user.getId();
        this.couponId = coupon.getId();
        this.isUsed = false;
        this.issuedAt = LocalDate.now();
        this.coupon = coupon;
    }

    public static UserCoupon of(Long userId, Long couponId) {
        return new UserCoupon(userId, couponId);
    }
    public static UserCoupon of(User user, Coupon coupon) {
        return new UserCoupon(user, coupon);
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

    public void rollback() {
        this.isUsed = false;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }
}
