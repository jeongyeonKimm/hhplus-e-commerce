package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.external.DataPlatformSender;
import kr.hhplus.be.server.application.external.dto.OrderData;
import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentFacade {

    private final PointService pointService;
    private final OrderService orderService;
    private final DataPlatformSender dataPlatformSender;

    public void payment(PaymentCommand command) {
        pointService.usePoint(command.getUserId(), command.getUseAmount());
        orderService.changeStatusToPaid(command.getOrderId());

        OrderData orderData = orderService.getOrderData(command.getOrderId());
        dataPlatformSender.send(orderData);
    }
}
