package kr.hhplus.be.server.application.coupon.dto.command;

import lombok.Getter;

@Getter
public class CouponGetCommand {

    private Long userId;

    private CouponGetCommand(Long userId) {
        this.userId = userId;
    }

    public static CouponGetCommand of(Long userId) {
        return new CouponGetCommand(userId);
    }
}
