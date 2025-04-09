package kr.hhplus.be.server.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    public Order createOrder(Order order, List<OrderProduct> orderProducts) {
        Order savedOrder = orderRepository.save(order);

        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrderId(savedOrder.getId());
            orderProductRepository.save(orderProduct);
        }

        return savedOrder;
    }
}
