package kr.hhplus.be.server.infrastructure.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestKafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("message publish: {}", message);
    }
}
