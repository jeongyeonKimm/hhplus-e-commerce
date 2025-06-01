package kr.hhplus.be.server.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.support.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static kr.hhplus.be.server.common.exception.ErrorCode.PARSING_ERROR;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String topic, DomainEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, message);
            log.info("Kafka 메시지 발행 성공: {}", event);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 발행 실패. JSON 파싱 오류: {}", event, e);
            throw new ApiException(PARSING_ERROR);
        }
    }

    public void publish(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
