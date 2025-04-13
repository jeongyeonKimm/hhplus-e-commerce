package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.external.dto.OrderData;
import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.use.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_ORDER;
import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_USER;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PointService pointService;
    private final ProductService productService;
    private final CouponService couponService;

    public Order createOrder(Long userId) {
        if (!userService.exists(userId)) {
            throw new ApiException(INVALID_USER);
        }
        return Order.of(userId);
    }

    public void saveOrder(Order order) {
        orderRepository.saveOrder(order);
    }

    public void addProduct(Order order, Product product, Long quantity) {
        order.addProduct(product, quantity);
    }

    public void applyCoupon(Order order, UserCoupon userCoupon) {
        order.applyCoupon(userCoupon);
    }

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
        LocalDate threshold = LocalDate.now().minus(duration);
        return orderRepository.findByStatusAndCreatedBefore(threshold);
    }

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
