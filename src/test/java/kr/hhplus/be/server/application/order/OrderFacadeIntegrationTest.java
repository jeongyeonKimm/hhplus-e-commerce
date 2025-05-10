package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
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

class OrderFacadeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("주문을 하면 주문 상품들의 재고 차감, 쿠폰 적용을 한다.")
    @Test
    void order() {
        User user = userRepository.save(User.of());
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

        OrderCreateCommand command = OrderCreateCommand.of(user.getId(), userCoupon.getId(), productInfos);

        OrderResult result = orderFacade.order(command);

        assertThat(result.getOrderId()).isNotNull();

        Order order = orderRepository.findOrderById(result.getOrderId()).get();
        assertThat(order.getTotalAmount()).isEqualTo(13000L);
    }
}
