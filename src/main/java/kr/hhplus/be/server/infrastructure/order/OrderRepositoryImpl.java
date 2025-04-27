package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public Order saveOrder(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public OrderProduct saveOrderProduct(OrderProduct orderProduct) {
        return orderProductJpaRepository.save(orderProduct);
    }

    @Override
    public List<OrderProduct> findOrderProductsByOrderId(Long orderId) {
        return orderProductJpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime before) {
        return orderJpaRepository.findByStatusAndCreatedAtBefore(status, before);
    }

    @Override
    public void saveAllOrderProducts(List<OrderProduct> orderProducts) {
        orderProductJpaRepository.saveAll(orderProducts);
    }

    @Override
    public List<Order> findPaidOrdersBetween(LocalDateTime start, LocalDateTime end) {
        return orderJpaRepository.findPaidOrdersBetween(start, end)
                .stream()
                .toList();
    }

    @Override
    public List<Order> findAllOrders() {
        return orderJpaRepository.findAll();
    }

    @Override
    public List<OrderProduct> findAllOrderProducts() {
        return orderProductJpaRepository.findAll();
    }
}
