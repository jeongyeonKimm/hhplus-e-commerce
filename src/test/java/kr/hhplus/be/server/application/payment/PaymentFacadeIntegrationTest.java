package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("결제를 하면 주문 금액을 포인트에서 차감, 주문 상태를 PAID로 변환한 뒤, 주문 이력을 외부 데이터 플랫폼에 전송한다.")
    @Test
    void payment() {
        long initialBalance = 20000L;
        User user = userRepository.save(User.of());
        Point point = pointRepository.savePoint(Point.of(user.getId(), initialBalance));

        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = startDate.plusYears(1);
        Coupon coupon = couponRepository.save(Coupon.of(
                "coupon1",
                1000L,
                DiscountType.AMOUNT,
                startDate,
                endDate,
                100L)
        );
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.of(user, coupon));

        Product product1 = productRepository.save(Product.of("product1", "sample product", 1000L, 100L));
        Product product2 = productRepository.save(Product.of("product2", "sample product", 2000L, 100L));
        Product product3 = productRepository.save(Product.of("product3", "sample product", 3000L, 100L));
        List<OrderProductInfo> productInfos = List.of(
                OrderProductInfo.of(product1.getId(), product1.getPrice(), 1L),
                OrderProductInfo.of(product2.getId(), product1.getPrice(), 2L),
                OrderProductInfo.of(product3.getId(), product1.getPrice(), 3L)
        );

        OrderCreateCommand orderCreateCommand = OrderCreateCommand.of(user.getId(), userCoupon.getId(), productInfos);
        OrderResult result = orderFacade.order(orderCreateCommand);

        PaymentCommand paymentCommand = PaymentCommand.of(result.getOrderId());

        paymentFacade.payment(paymentCommand);

        Point usedPoint = pointRepository.findPointByUserId(user.getId()).get();
        assertThat(usedPoint.getBalance()).isEqualTo(7000L);

        Order order = orderRepository.findOrderById(result.getOrderId()).get();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }
}
