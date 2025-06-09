package kr.hhplus.be.server.interfaces.event.payment;

import kr.hhplus.be.server.domain.payment.PaymentOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentOutboxScheduler {

    private final PaymentOutboxService paymentOutboxService;

    @Scheduled(fixedDelay = 5000)
    public void publishOrderDataOutboxEvent() {
        paymentOutboxService.republishOutboxEvent();
    }
}
