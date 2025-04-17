package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order saveOrder(Order order);

    Optional<Order> findOrderById(Long orderId);

    void saveOrderProduct(OrderProduct orderProduct);

    List<OrderProduct> findOrderProductsByOrderId(Long orderId);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdBefore);

    void saveAllOrderProducts(List<OrderProduct> orderProducts);

    List<Order> findPaidOrdersBetween(LocalDateTime start, LocalDateTime end);
}
