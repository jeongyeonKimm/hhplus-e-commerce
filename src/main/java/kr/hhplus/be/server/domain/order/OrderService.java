package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
import kr.hhplus.be.server.domain.product.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_ORDER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional
    public Order createOrder(Long userId) {
        return Order.of(userId);
    }

    @Transactional
    public void saveOrder(Order order) {
        orderRepository.saveOrder(order);
    }

    @Transactional
    public void addProduct(Order order, Product product, Long quantity) {
        if (order.getId() == null) {
            orderRepository.saveOrder(order);
        }

        OrderProduct orderProduct = OrderProduct.of(order, product, quantity);
        order.addProduct(product, orderProduct);

        orderRepository.saveAllOrderProducts(order.getOrderProducts());
    }

    @Transactional
    public void applyCoupon(Order order, UserCoupon userCoupon) {
        order.applyCoupon(userCoupon);
        orderRepository.saveOrder(order);
    }

    @Transactional
    public void changeStatusToPaid(Order order) {
        order.pay();
        orderRepository.saveOrder(order);
    }

    @Transactional
    public void expireOrder(Order order, List<Product> products) {
        order.expired(products);
        orderRepository.saveOrder(order);
    }

    @Transactional
    public void sendOrderData(Long orderId) {
        Order order = orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ApiException(INVALID_ORDER));

        List<OrderProduct> orderProducts = orderRepository.findOrderProductsByOrderId(orderId);

        PaymentEvent.Completed event = PaymentEvent.Completed.from(order, orderProducts);
        paymentEventPublisher.publish(event);
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new ApiException(INVALID_ORDER));
    }

    public List<Order> getUnpaidOrdersExceed(Duration duration) {
        LocalDateTime threshold = LocalDateTime.now().minus(duration);
        return orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.NOT_PAID, threshold);
    }

    public List<OrderProduct> getOrderProducts(Long orderId) {
        return orderRepository.findOrderProductsByOrderId(orderId);
    }
}
