package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@SpringBootTest
public class OrderConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("여러 사용자가 동시에 같은 상품을 주문해도 상품의 재고를 초과해서 주문이 되지 않는다.")
    @Test
    void order_concurrently() throws InterruptedException {
        int threadCount = 20;
        long initialStock = 10L;
        long productId = 2L;
        long price = 1000L;
        long quantity = 1L;

        Product product = Instancio.of(Product.class)
                .set(field(Product::getStock), initialStock)
                .create();
        productRepository.save(product);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            long userId = i + 1;
            executor.submit(() -> {
                try {
                    OrderCreateCommand command = OrderCreateCommand.of(
                            userId,
                            null,
                            List.of(OrderProductInfo.of(productId, price, quantity))
                    );
                    orderFacade.order(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        List<Order> orders = orderRepository.findAllOrders();

        long totalOrderedQuantity = orders.stream()
                .flatMap(o -> o.getOrderProducts().stream())
                .filter(op -> op.getProductId().equals(productId))
                .mapToLong(OrderProduct::getQuantity)
                .sum();

        assertThat(product.getStock()).isGreaterThanOrEqualTo(0);
        assertThat(totalOrderedQuantity).isEqualTo(initialStock);
    }
}
