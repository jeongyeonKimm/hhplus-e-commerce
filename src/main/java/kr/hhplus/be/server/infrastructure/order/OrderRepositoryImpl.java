package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    public void saveOrderProduct(OrderProduct orderProduct) {
        orderProductJpaRepository.save(orderProduct);
    }

    @Override
    public List<OrderProduct> findOrderProductByOrderId(Long orderId) {
        return orderProductJpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDate before) {
        return orderJpaRepository.findByStatusAndCreatedAtBefore(status, before);
    }

    @Override
    public void saveAllOrderProducts(List<OrderProduct> orderProducts) {
        orderProductJpaRepository.saveAll(orderProducts);
    }
}
