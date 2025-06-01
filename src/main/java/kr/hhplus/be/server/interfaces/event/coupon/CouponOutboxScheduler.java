package kr.hhplus.be.server.interfaces.event.coupon;

import kr.hhplus.be.server.domain.coupon.CouponOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CouponOutboxScheduler {

    private final CouponOutboxService couponOutboxService;

    @Scheduled(fixedDelay = 1000)
    public void publishCouponOutboxEvent() {
        couponOutboxService.republishCouponOutbox();
    }
}
