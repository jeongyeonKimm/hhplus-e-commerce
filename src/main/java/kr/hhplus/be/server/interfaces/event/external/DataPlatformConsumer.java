package kr.hhplus.be.server.interfaces.event.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.external.DataPlatformSender;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.payment.PaymentOutbox;
import kr.hhplus.be.server.domain.payment.PaymentOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class DataPlatformConsumer {

    private final PaymentOutboxService paymentOutboxService;
    private final DataPlatformSender dataPlatformSender;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "order-data", groupId = "order-data-group")
    public void consume(String message) {
        PaymentEvent.Completed event  = null;
        try {
            event = objectMapper.readValue(message, PaymentEvent.Completed.class);
            dataPlatformSender.send(event.toString());

            PaymentOutbox outbox = paymentOutboxService.getOutbox(event);
            outbox.markAsSuccess();

            log.info("Kafka 메시지 수신 성공: {}", event);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 수신 실패. JSON 파싱 오류: {}", event, e);
            throw new ApiException(ErrorCode.PARSING_ERROR);
        } catch (InterruptedException e) {
            log.error("Kafka 메시지 수신 실패: {}", event);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
