package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class PaymentFacade {

    private final PointService pointService;
    private final OrderService orderService;

    @Transactional
    public void payment(PaymentCommand command) {
        Order order = orderService.getOrder(command.getOrderId());

        pointService.usePoint(order.getUserId(), order.getTotalAmount());
        orderService.changeStatusToPaid(order);

        orderService.sendOrderData(command.getOrderId());
    }
}
