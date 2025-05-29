package kr.hhplus.be.server.interfaces.event.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.coupon.CouponEvent;
import kr.hhplus.be.server.domain.coupon.CouponIssueProcessor;
import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@RequiredArgsConstructor
@Component
public class CouponConsumer {

    private final OutboxService outboxService;
    private final CouponIssueProcessor couponIssueProcessor;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(
            topics = "coupon-reserved",
            groupId = "coupon-group",
            containerFactory = "batchContainerFactory"
    )
    public void consume(List<String> messages) {
        log.info("[CouponConsumer] 배치 처리 시작: {}건", messages.size());

        try {
            List<CouponEvent.Reserved> events = messages.stream()
                    .map(this::deserialize)
                    .toList();

            couponIssueProcessor.processCouponIssuance(events);

            List<Outbox> outboxes = outboxService.getAllOutboxesByReservedEvent(events);
            outboxes.forEach(Outbox::markAsSuccess);

            log.info("Kafka 메시지 수신 성공");
        } catch (Exception e) {
            log.error("Kafka 메시지 수신 실패", e);
            throw new ApiException(INTERNAL_SERVER_ERROR);
        }

        log.info("[CouponConsumer] 배치 처리 종료");
    }

    private CouponEvent.Reserved deserialize(String json) {
        try {
            return objectMapper.readValue(json, CouponEvent.Reserved.class);
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 실패", e);
        }
    }
}
