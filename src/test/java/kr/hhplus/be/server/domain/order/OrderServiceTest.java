package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_ORDER;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_USER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

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
}
