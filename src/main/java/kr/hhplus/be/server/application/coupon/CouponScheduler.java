package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssueProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponScheduler {

    private final CouponIssueProcessor couponIssueProcessor;

    @Async
    @Scheduled(fixedDelay = 10000)
    public void issueCoupon() {
        couponIssueProcessor.processCouponIssuance();

        log.info("[CouponScheduler] 쿠폰 발급 스케줄러 완료");
    }
}
