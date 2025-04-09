package kr.hhplus.be.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_CHARGE_AMOUNT("INVALID_CHARGE_AMOUNT", 400, "유효하지 않은 충전 금액입니다."),
    CHARGE_AMOUNT_EXCEEDS_LIMIT("CHARGE_AMOUNT_EXCEEDS_LIMIT", 400,"최대 누적 충전 금액을 초과하였습니다."),

    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", 400, "재고가 부족합니다."),
    INVALID_PRODUCT("INVALID_PRODUCT", 404, "유효하지 않은 상품입니다."),

    INVALID_COUPON("INVALID_COUPON", 404, "유효하지 않은 쿠폰입니다."),
    ALREADY_USED_COUPON("ALREADY_USED_COUPON", 409, "이미 사용된 쿠폰입니다."),
    COUPON_DATE_EXPIRED("COUPON_DATE_EXPIRED", 409, "쿠폰이 만료되었습니다."),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 500, "서버 내부 오류입니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
