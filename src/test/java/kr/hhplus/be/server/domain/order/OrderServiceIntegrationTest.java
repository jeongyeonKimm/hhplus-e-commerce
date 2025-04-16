package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.external.dto.OrderData;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private PointRepository pointRepository;

    @DisplayName("사용자 ID를 이용해 주문을 생성한다.")
    @Test
    void createOrder() {
        User user = userRepository.save(User.of());

        Order order = orderService.createOrder(user.getId());

        assertThat(order.getUserId()).isEqualTo(user.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.NOT_PAID);
    }

    @DisplayName("주문에 상품을 상품 수량만큼 추가한다.")
    @Test
    void addProduct() {
        User user = userRepository.save(User.of());
        Order order = orderService.createOrder(user.getId());
        long price = 10000L;
        Product product = productRepository.save(Product.of("product1", "sample product".getBytes(), price, 100L));

        long quantity = 10L;
        orderService.addProduct(order, product, quantity);

        assertThat(order.getOrderProducts()).hasSize(1);
        assertThat(order.getTotalAmount()).isEqualTo(price * quantity);
    }

    @DisplayName("주문과 사용자 쿠폰을 받아 주문에 쿠폰을 적용한다.")
    @Test
    void applyCoupon() {
        long price = 10000L;
        User user = userRepository.save(User.of());
        Product product = productRepository.save(Product.of("product1", "sample product".getBytes(), price, 100L));

        Order order = orderService.createOrder(user.getId());
        long quantity = 10L;
        orderService.addProduct(order, product, quantity);

        Coupon coupon = couponRepository.save(Coupon.of(
                "coupon1",
                1000L,
                DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 30),
                100L)
        );
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.of(user, coupon));

        orderService.applyCoupon(order, userCoupon);

        long expectedAmount = price * quantity - coupon.getDiscountValue();
        assertThat(order.getIsCouponApplied()).isTrue();
        assertThat(order.getUserCouponId()).isEqualTo(userCoupon.getId());
        assertThat(order.getTotalAmount()).isEqualTo(expectedAmount);
        assertThat(userCoupon.getIsUsed()).isTrue();
    }

    @DisplayName("주문의 상태를 PAID로 바꾼다.")
    @Test
    void changeStatusToPaid() {
        User user = userRepository.save(User.of());
        Order order = orderService.createOrder(user.getId());

        orderService.changeStatusToPaid(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @DisplayName("주문 상태가 NOT_PAID이고 주어진 시간을 초과한 주문을 조회한다.")
    @Test
    void getUnpaidOrdersExceed() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusNanos(500);

        Duration duration = Duration.between(before, now);

        User user = userRepository.save(User.of());
        Order order1 = Order.of(user.getId());
        orderRepository.saveOrder(order1);

        Order order2 = Order.of(user.getId());
        orderRepository.saveOrder(order2);
        orderService.changeStatusToPaid(order2);

        List<Order> orders = orderService.getUnpaidOrdersExceed(duration);

        assertThat(orders).hasSize(1);
    }

    @DisplayName("주문을 만료 처리하고 재고, 포인트, 쿠폰 사용 여부를 롤백한다.")
    @Test
    void expireOrder() {
        User user = userRepository.save(User.of());
        long balance = 10000L;
        Point point = pointRepository.savePoint(Point.of(user.getId(), balance));
        Order order = orderService.createOrder(user.getId());

        Product product1 = productRepository.save(Product.of("product1", "sample product".getBytes(), 1000L, 100L));
        orderService.addProduct(order, product1, 1L);
        Product product2 = productRepository.save(Product.of("product2", "sample product".getBytes(), 2000L, 100L));
        orderService.addProduct(order, product2, 1L);
        Product product3 = productRepository.save(Product.of("product3", "sample product".getBytes(), 3000L, 100L));
        orderService.addProduct(order, product3, 1L);

        orderService.expireOrder(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.EXPIRED);
        long expected = balance + product1.getPrice() + product2.getPrice() + product3.getPrice();
        assertThat(point.getBalance()).isEqualTo(expected);
    }
}
