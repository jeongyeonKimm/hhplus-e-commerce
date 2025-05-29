package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.point.PointService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.instancio.Select.field;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @InjectMocks
    private PaymentFacade paymentFacade;

    @Mock
    private PointService pointService;

    @Mock
    private OrderService orderService;

    @DisplayName("결제 성공 시 포인트 차감, 주문 상태 변경, 외부 데이터 플랫폼에 주문 정보 전송이 수행된다.")
    @Test
    void pay() {
        long orderId = 1L;
        long userId = 2L;
        long totalAmount = 1000L;

        PaymentCommand command = PaymentCommand.of(orderId);
        Order order = Instancio.of(Order.class)
                .set(field("userId"), userId)
                .set(field("totalAmount"), totalAmount)
                .create();

        given(orderService.getOrder(orderId)).willReturn(order);

        paymentFacade.pay(command);

        verify(orderService, times(1)).getOrder(orderId);
        verify(pointService, times(1)).usePoint(userId, totalAmount);
        verify(orderService, times(1)).changeStatusToPaid(order);
    }
}
