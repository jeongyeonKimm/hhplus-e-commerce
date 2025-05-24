package kr.hhplus.be.server.interfaces.event.external;

import kr.hhplus.be.server.application.external.DataPlatformSender;
import kr.hhplus.be.server.domain.payment.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@RequiredArgsConstructor
@Component
public class DataPlatformPaymentEventListener {

    private final DataPlatformSender sender;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void handle(PaymentEvent.Completed event) throws InterruptedException {
        // 외부 데이터 플랫폼 전송
        sender.send(event.toString());
    }
}
