package kr.hhplus.be.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_USER("INVALID_USER", 404, "유효하지 않은 사용자입니다."),

    INVALID_CHARGE_AMOUNT("INVALID_CHARGE_AMOUNT", 400, "유효하지 않은 충전 금액입니다."),
    INVALID_USE_AMOUNT("INVALID_USE_AMOUNT", 400, "유효하지 않은 사용 금액입니다."),
    CHARGE_AMOUNT_EXCEEDS_LIMIT("CHARGE_AMOUNT_EXCEEDS_LIMIT", 400,"최대 누적 충전 금액을 초과하였습니다."),
    POINT_NOT_EXIST("POINT_NOT_EXIST", 404, "포인트가 존재하지 않습니다."),
    INVALID_RESTORE_AMOUNT("INVALID_RESTORE_AMOUNT", 400, "복원되는 포인트는 0 이하일 수 없습니다."),
    DUPLICATE_CHARGE("DUPLICATE_CHARGE", 400, "이미 충전된 요청입니다."),
    DUPLICATE_USE("DUPLICATE_USE", 400, "이미 사용된 요청입니다."),

    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", 400, "재고가 부족합니다."),
    INVALID_PRODUCT("INVALID_PRODUCT", 404, "유효하지 않은 상품입니다."),
    EMPTY_ORDER_PRODUCTS("EMPTY_ORDER_PRODUCTS", 400, "주문 상품은 비어있을 수 없습니다."),
    INVALID_RESTORE_QUANTITY("INVALID_RESTORE_QUANTITY", 400, "재고 복원 수량이 0 이하일 수 없습니다."),

    COUPON_NOT_OWNED("COUPON_NOT_OWNED", 404, "유저가 보유하고 있는 쿠폰이 아닙니다."),
    ALREADY_USED_COUPON("ALREADY_USED_COUPON", 409, "이미 사용된 쿠폰입니다."),
    COUPON_DATE_EXPIRED("COUPON_DATE_EXPIRED", 409, "쿠폰이 만료되었습니다."),
    INVALID_COUPON("INVALID_COUPON", 404, "존재하지 않는 쿠폰입니다."),
    INSUFFICIENT_COUPON_STOCK("INSUFFICIENT_COUPON", 409, "쿠폰이 모두 소진되었습니다."),
    COUPON_ALREADY_ISSUED("COUPON_ALREADY_ISSUED", 409, "이미 발급 받은 쿠폰입니다."),
    INVALID_USER_COUPON("INVALID_USER_COUPON", 404, "유효하지 않은 사용자 쿠폰입니다."),

    INVALID_ORDER("INVALID_ORDER", 404, "유효하지 않은 주문입니다."),
    ORDER_PAYMENT_INVALID_STATE("ORDER_PAYMENT_INVALID_STATE", 400, "결제가 불가능한 상태입니다."),
    COUPON_ALREADY_APPLIED("COUPON_ALREADY_APPLIED", 400, "이미 쿠폰이 적용된 주문입니다."),
    INVALID_ORDER_PRODUCTS("INVALID_ORDER_PRODUCTS", 400, "주문 상품은 null일 수 없습니다."),

    INVALID_DATE_TIME("INVALID_DATE_TIME", 400, "유효하지 않은 날짜입니다."),

    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", 500, "서버 내부 오류입니다."),
    LOCK_INTERRUPTED("LOCK_INTERRUPTED", 500, "락 인터럽트가 발생하였습니다."),
    LOCK_NOT_AVAILABLE("LOCK_NOT_AVAILABLE", 500, "락을 획득할 수 없습니다."),
    PARSING_ERROR("PARSING_ERROR", 500, "SpEL 파싱 중 오류가 발생하였습니다");

    private final String code;
    private final int httpStatus;
    private final String message;
}
