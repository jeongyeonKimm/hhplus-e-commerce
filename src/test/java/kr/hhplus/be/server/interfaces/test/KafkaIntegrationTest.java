package kr.hhplus.be.server.interfaces.test;

import kr.hhplus.be.server.infrastructure.test.TestKafkaPublisher;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@ExtendWith(OutputCaptureExtension.class)
class KafkaIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private TestKafkaPublisher testKafkaPublisher;

    @Test
    void publishAndConsume(CapturedOutput output) {
        String message = "Kafka Test Message";

        testKafkaPublisher.publish("test-topic", message);

        await()
                .atMost(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertTrue(output.toString().contains("message publish: " + message));
                    assertTrue(output.toString().contains("message consume: " + message));
                });
    }
}
