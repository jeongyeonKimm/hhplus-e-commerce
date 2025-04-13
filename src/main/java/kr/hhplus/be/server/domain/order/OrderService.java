package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.external.dto.OrderData;
import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_ORDER;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(Order order, List<OrderProduct> orderProducts) {
        Order savedOrder = orderRepository.saveOrder(order);

        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrderId(savedOrder.getId());
            orderRepository.saveOrderProduct(orderProduct);
        }

        return savedOrder;
    }

    public void changeStatusToPaid(Long orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ApiException(INVALID_ORDER));

        order.changeStatus(OrderStatus.PAID);
        orderRepository.saveOrder(order);
    }

    public OrderData getOrderData(Long orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ApiException(INVALID_ORDER));

        List<OrderProduct> orderProducts = orderRepository.findOrderProductByOrderId(orderId);

        return OrderData.from(order, orderProducts);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ApiException(INVALID_ORDER));
    }
}
