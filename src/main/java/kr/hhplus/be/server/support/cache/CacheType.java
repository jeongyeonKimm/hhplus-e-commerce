package kr.hhplus.be.server.support.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public enum CacheType {

    DAY3_BEST_SELLERS("3days-best-sellers", Duration.ofHours(25)),
    PRODUCT_DAILY_SALES("daily-sales", Duration.ofDays(30));

    private final String cacheName;
    private final Duration ttl;
}
