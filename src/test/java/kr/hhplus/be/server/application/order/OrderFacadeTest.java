package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.application.order.dto.OrderProductList;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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
        Long userId = 1L;
        Long userCouponId = 2L;
        int totalAmount = 400;
        int finalAmount = 200;

        List<OrderProductInfo> productInfos = List.of(
                OrderProductInfo.of(3L, 100, 2),
                OrderProductInfo.of(4L, 200, 1)
        );

        OrderProductList orderProductList = OrderProductList.of(productInfos);
        OrderCreateCommand command = OrderCreateCommand.of(userId, userCouponId, orderProductList);

        Order order = Order.of(5L, userId, userCouponId, true, totalAmount);

        given(couponService.redeemCoupon(userId, userCouponId)).willReturn(true);
        given(couponService.calculateFinalAmount(userCouponId, totalAmount)).willReturn(finalAmount);
        given(orderService.createOrder(any(Order.class), anyList())).willReturn(order);

        OrderResult result = orderFacade.order(command);

        assertThat(result.getOrderId()).isEqualTo(order.getId());

        verify(productService, times(1)).deductStock(3L, 2);
        verify(productService, times(1)).deductStock(4L, 1);
        verify(couponService, times(1)).redeemCoupon(userId, userCouponId);
        verify(couponService, times(1)).calculateFinalAmount(userCouponId, totalAmount);
        verify(orderService, times(1)).createOrder(any(Order.class), anyList());
    }
}
