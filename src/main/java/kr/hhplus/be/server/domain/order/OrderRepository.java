package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order saveOrder(Order order);

    Optional<Order> findOrderById(Long orderId);

    void saveOrderProduct(OrderProduct orderProduct);

    List<OrderProduct> findOrderProductByOrderId(Long orderId);
}
