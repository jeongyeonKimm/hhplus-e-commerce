package kr.hhplus.be.server.interfaces.event.outbox;

import kr.hhplus.be.server.domain.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OutboxScheduler {

    private final OutboxService outboxService;

    @Scheduled(fixedDelay = 1000)
    public void publishOutboxEvent() {
        outboxService.republishOutboxEvent();
    }
}
