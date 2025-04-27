package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSellerService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BestSellerSchedulerTest {

    @InjectMocks
    private BestSellerScheduler bestSellerScheduler;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private BestSellerService bestSellerService;

    @DisplayName("한 시간마다 결제가 완료된 주문 상품의 판매량을 집계한다.")
    @Test
    void calculateHourlyBestSellers() {
        OrderProduct orderProduct1 = Instancio.of(OrderProduct.class)
                .set(field(OrderProduct::getProductId), 1L)
                .set(field(OrderProduct::getQuantity), 10L)
                .create();
        OrderProduct orderProduct2 = Instancio.of(OrderProduct.class)
                .set(field(OrderProduct::getProductId), 2L)
                .set(field(OrderProduct::getQuantity), 10L)
                .create();
        OrderProduct orderProduct3 = Instancio.of(OrderProduct.class)
                .set(field(OrderProduct::getProductId), 1L)
                .set(field(OrderProduct::getQuantity), 10L)
                .create();

        Order order1 = Instancio.of(Order.class)
                .set(field(Order::getStatus), OrderStatus.PAID)
                .set(field(Order::getOrderProducts), List.of(orderProduct1, orderProduct2))
                .create();
        Order order2 = Instancio.of(Order.class)
                .set(field(Order::getStatus), OrderStatus.PAID)
                .set(field(Order::getOrderProducts), List.of(orderProduct3))
                .create();

        List<Order> orders = List.of(order1, order2);
        given(orderService.findPaidOrdersBetween(any(), any())).willReturn(orders);

        Product product1 = Instancio.of(Product.class)
                .set(field(Product::getId), 1L)
                .create();
        Product product2 = Instancio.of(Product.class)
                .set(field(Product::getId), 2L)
                .create();
        given(productService.getProductWithLock(1L)).willReturn(product1);
        given(productService.getProductWithLock(2L)).willReturn(product2);

        bestSellerScheduler.calculateHourlyBestSellers();

        verify(bestSellerService).save(argThat(bs ->
                bs.getProductId().equals(1L) &&
                        bs.getSales() == 20L
        ));
        verify(bestSellerService).save(argThat(bs ->
                bs.getProductId().equals(2L) &&
                        bs.getSales() == 10L
        ));
        verify(productService, times(1)).getProductWithLock(1L);
        verify(productService, times(1)).getProductWithLock(2L);
    }

    @DisplayName("3일이 지난 데이터를 매일 새벽 00:05")
    @Test
    void deleteOldBestSellers() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedThreshold = now.minusDays(3);

        // when
        bestSellerScheduler.deleteOldBestSellers();

        // then
        verify(bestSellerService).deleteByCreatedAtBefore(argThat(actual -> {
            Duration diff = Duration.between(actual, expectedThreshold);
            return Math.abs(diff.toSeconds()) < 2;
        }));
    }

}
