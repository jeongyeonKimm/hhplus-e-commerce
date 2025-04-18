package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderExpireSchedulerTest {

    @InjectMocks
    private OrderExpireScheduler orderExpireScheduler;

    @Mock
    private OrderService orderService;

    @DisplayName("결제가 되지 않고 5분이 지난 주문은 만료 처리를 한다.")
    @Test
    void expireUnpaidOrder() {
        Order order1 = Instancio.of(Order.class)
                .set(field("status"), OrderStatus.NOT_PAID)
                .create();
        Order order2 = Instancio.of(Order.class)
                .set(field("status"), OrderStatus.NOT_PAID)
                .create();
        List<Order> expiredOrders = List.of(order1, order2);

        Duration duration = Duration.ofMinutes(5);
        given(orderService.getUnpaidOrdersExceed(duration)).willReturn(expiredOrders);

        orderExpireScheduler.expireUnpaidOrders();

        verify(orderService, times(1)).getUnpaidOrdersExceed(duration);
        verify(orderService, times(1)).expireOrder(order1);
        verify(orderService, times(1)).expireOrder(order2);
    }
}
