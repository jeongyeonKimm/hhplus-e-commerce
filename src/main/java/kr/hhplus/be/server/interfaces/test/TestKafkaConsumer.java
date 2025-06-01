package kr.hhplus.be.server.interfaces.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestKafkaConsumer {

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consume(String message) {
        log.info("message consume: {}", message);
    }
}
