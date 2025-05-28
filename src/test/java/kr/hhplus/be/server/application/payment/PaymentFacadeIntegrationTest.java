package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.external.DataPlatformSender;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.payment.PaymentEvent;
import kr.hhplus.be.server.domain.payment.PaymentEventPublisher;
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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RecordApplicationEvents
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

    @Autowired
    private ApplicationEvents events;

    @MockitoSpyBean
    private PaymentEventPublisher eventPublisher;

    @DisplayName("결제를 하면 주문 금액을 포인트에서 차감, 주문 상태를 PAID로 변환한 뒤, 결제 완료 이벤트를 발행한다")
    @Test
    void pay() {
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

        paymentFacade.pay(paymentCommand);

        Point usedPoint = pointRepository.findPointByUserId(user.getId()).orElseThrow();
        assertThat(usedPoint.getBalance()).isEqualTo(7000L);

        Order order = orderRepository.findOrderById(result.getOrderId()).orElseThrow();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        long eventCount = events.stream(PaymentEvent.Completed.class).count();
        assertThat(eventCount).isEqualTo(1);

        verify(eventPublisher, times(1)).publish(any(PaymentEvent.Completed.class));
    }
}
