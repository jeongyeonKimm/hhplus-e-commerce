package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
public class PaymentConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("한명의 사용자가 결제를 동시에 여러번 요청한 경우 한번만 성공한다.")
    @Test
    void payment_concurrency() {
        int threadCount = 4;
        long userId = 1L;
        long initialBalance = 10000L;
        long price = 1000L;

        pointRepository.savePoint(Instancio.of(Point.class)
                .set(field(Point::getUserId), userId)
                .set(field(Point::getBalance), initialBalance)
                .create());

        Order order = orderService.createOrder(userId);
        orderService.addProduct(order, Product.of("product1", "This is product", price, 10L), 1L);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    PaymentCommand command = PaymentCommand.of(order.getId());
                    paymentFacade.payment(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        Order paidOrder = orderRepository.findOrderById(order.getId()).get();
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.PAID);

        Point point = pointRepository.findPointByUserId(userId).get();
        assertThat(point.getBalance()).isEqualTo(initialBalance - price);
    }
}
