package kr.hhplus.be.server.domain.coupon;

public class CouponKey {

    private static final String COUPON_RESERVE_KEY = "coupon:reserve";
    private static final String COUPON_SUCCESS_ISSUANCE_KEY = "coupon:success:issuance";
    private static final String COUPON_FAIL_ISSUANCE_KEY = "coupon:fail:issuance";

    public static String getCouponReserveKey(Long couponId) {
        return COUPON_RESERVE_KEY + couponId;
    }

    public static String getCouponSuccessIssuanceKey(Long couponId) {
        return COUPON_SUCCESS_ISSUANCE_KEY + couponId;
    }

    public static String getCouponFailIssuanceKey(Long couponId) {
        return COUPON_FAIL_ISSUANCE_KEY + couponId;
    }
}
