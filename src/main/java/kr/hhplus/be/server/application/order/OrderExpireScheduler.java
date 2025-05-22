package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderExpireCommand;
import kr.hhplus.be.server.domain.order.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderExpireScheduler {

    private final OrderFacade orderFacade;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expireUnpaidOrders() {
        List<Order> unpaidOrders = orderFacade.getUnpaidOrdersExceed(Duration.ofMinutes(5));

        long successCount = 0;
        long failCount = 0;

        for (Order order : unpaidOrders) {
            try {
                orderFacade.expire(OrderExpireCommand.of(order));
                successCount++;
            } catch (Exception e) {
                failCount++;
                log.warn("[OrderExpireScheduler] 주문 만료 처리 실패: orderId = {}, message = {}", order.getId(), e.getMessage());
            }
        }

        log.info("[OrderExpireScheduler] 주문 만료 처리 완료: 성공 {}건, 실패 {}건", successCount, failCount);
    }
}
