package kr.hhplus.be.server.application.external;

import org.springframework.stereotype.Service;

@Service
public class DataPlatformSender {

    public void send(String data) throws InterruptedException {
        // 데이터 전송
        Thread.sleep(2000);
    }
}
