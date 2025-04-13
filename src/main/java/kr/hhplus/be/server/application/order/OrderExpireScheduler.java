package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderExpireScheduler {

    private final OrderService orderService;

    @Scheduled(fixedDelay = 60_000)
    public void expireUnpaidOrders() {
        List<Order> unpaidOrders = orderService.getUnpaidOrdersExceed(Duration.ofMinutes(5));

        for (Order order : unpaidOrders) {
            try {
                orderService.expireOrder(order);
            } catch (Exception e) {
                log.warn("[주문 만료 처리 실패] orderId = {}, message = {}", order.getId(), e.getMessage());
            }
        }
    }
}
