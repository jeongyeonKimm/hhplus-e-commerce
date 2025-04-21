package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.external.DataPlatformSender;
import kr.hhplus.be.server.application.external.dto.OrderData;
import kr.hhplus.be.server.application.external.dto.OrderProductData;
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

import java.util.List;

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

    @Mock
    private DataPlatformSender dataPlatformSender;

    @DisplayName("결제 시 포인트 차감, 주문 상태 변경, 외부 데이터 플랫폼에 주문 정보 전송이 수행된다.")
    @Test
    void payment() {
        long orderId = 1L;
        long userId = 2L;
        long userCouponId = 3L;
        long totalAmount = 1000L;

        PaymentCommand command = PaymentCommand.of(orderId);
        Order order = Instancio.of(Order.class)
                .set(field("userId"), userId)
                .set(field("totalAmount"), totalAmount)
                .create();

        List<OrderProductData> orderDataList = List.of(
                OrderProductData.of(4L, 1000L, 1L),
                OrderProductData.of(5L, 2000L, 1L)
        );
        OrderData orderData = OrderData.of(orderId, userId, userCouponId, true, totalAmount, orderDataList);

        given(orderService.getOrder(orderId)).willReturn(order);
        given(orderService.getOrderData(orderId)).willReturn(orderData);

        paymentFacade.payment(command);

        verify(orderService, times(1)).getOrder(orderId);
        verify(pointService, times(1)).usePoint(userId, totalAmount);
        verify(orderService, times(1)).changeStatusToPaid(order);
        verify(dataPlatformSender, times(1)).send(orderData);
    }
}
