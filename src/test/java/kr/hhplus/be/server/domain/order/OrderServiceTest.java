package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_ORDER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @DisplayName("유효하지 않은 주문 ID로 주문 데이터를 조회하면 데이터 조회에 실패하고 InvalidOrderException이 발생한다.")
    @Test
    void getOrderData_throwInvalidOrder_whenOrderIsInvalid() {
        long orderId = 1L;
        given(orderRepository.findOrderById(orderId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.sendOrderData(orderId))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_ORDER.getMessage());
    }

    @DisplayName("유효하지 않은 주문 ID로 주문을 조회하면 주문 조회에 실패하고 InvalidOrderException이 발생한다.")
    @Test
    void getOrder_throwInvalidOrder_whenOrderIsInvalid() {
        long orderId = 1L;
        given(orderRepository.findOrderById(orderId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_ORDER.getMessage());
    }

    @DisplayName("주문 데이터를 외부 데이터 플랫폼에 전송한다.")
    @Test
    void sendOrderData() {
        long orderId = 1L;
        Order order = Instancio.of(Order.class)
                .set(field(Order::getId), orderId)
                .create();
        given(orderRepository.findOrderById(orderId)).willReturn(Optional.of(order));

        orderService.sendOrderData(orderId);

        verify(paymentEventPublisher, times(1)).publish(any(PaymentEvent.Completed.class));
    }
}
