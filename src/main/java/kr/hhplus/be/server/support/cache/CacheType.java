package kr.hhplus.be.server.support.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    DAY3_BEST_SELLERS("최근 3일 인기 상품 캐시", Duration.ofHours(25)),
    PRODUCT_DAILY_SALES("1일 상품 판매량 캐시", Duration.ofDays(30));

    private final String cacheName;
    private final Duration ttl;
}
