package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @InjectMocks
    private OrderFacade orderFacade;

    @Mock
    private ProductService productService;

    @Mock
    private CouponService couponService;

    @Mock
    private OrderService orderService;

    @DisplayName("주문 생성 시 상품 재고 차감, 쿠폰 적용, 주문 생성이 수행된다.")
    @Test
    void order() {
        long userId = 1L;
        long userCouponId = 2L;
        long couponId = 5L;

        List<OrderProductInfo> productInfos = List.of(
                OrderProductInfo.of(3L, 100L, 2L),
                OrderProductInfo.of(4L, 200L, 1L)
        );

        OrderCreateCommand command = OrderCreateCommand.of(userId, userCouponId, productInfos);

        Order order = Order.of(userId);
        Product product1 = Product.of("iPhone 13", "Apple iPhone 13".getBytes(), 1_000_000L, 100L);
        Product product2 = Product.of("iPhone 15", "Apple iPhone 15".getBytes(), 1_500_000L, 100L);
        UserCoupon userCoupon = UserCoupon.of(userId, couponId);

        given(orderService.createOrder(userId)).willReturn(order);
        given(productService.getProduct(3L)).willReturn(product1);
        given(productService.getProduct(4L)).willReturn(product2);
        given(couponService.getUserCoupon(userCouponId)).willReturn(userCoupon);

        OrderResult result = orderFacade.order(command);

        assertThat(result.getOrderId()).isEqualTo(order.getId());

        verify(orderService, times(1)).createOrder(userId);
        verify(productService, times(1)).getProduct(3L);
        verify(productService, times(1)).getProduct(4L);
        verify(orderService, times(1)).addProduct(order, product1, 2L);
        verify(orderService, times(1)).addProduct(order, product2, 1L);
        verify(couponService, times(1)).getUserCoupon(userCouponId);
        verify(orderService, times(1)).applyCoupon(order, userCoupon);
    }
}
