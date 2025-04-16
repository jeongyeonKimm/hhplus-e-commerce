package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.external.dto.OrderData;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_ORDER;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_USER;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PointService pointService;
    private final ProductService productService;
    private final CouponService couponService;

    @Transactional
    public Order createOrder(Long userId) {
        if (!userService.exists(userId)) {
            throw new ApiException(INVALID_USER);
        }
        return Order.of(userId);
    }

    @Transactional
    public void saveOrder(Order order) {
        orderRepository.saveOrder(order);
    }

    @Transactional
    public void addProduct(Order order, Product product, Long quantity) {
        order.addProduct(product, quantity);

        orderRepository.saveOrder(order);
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

    public List<Order> getUnpaidOrdersExceed(Duration duration) {
        LocalDateTime threshold = LocalDateTime.now().minus(duration);
        return orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.NOT_PAID, threshold);
    }

    @Transactional
    public void expireOrder(Order order) {
        List<Product> products = productService.getAllProductsByIds(order.getProductIds());
        order.expired(products);

        pointService.rollbackPoint(order.getUserId(), order.getTotalAmount());

        if (order.getIsCouponApplied()) {
            couponService.rollbackCoupon(order.getUserCouponId());
        }

        orderRepository.saveOrder(order);
    }
}
