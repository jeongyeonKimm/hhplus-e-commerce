package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("2명의 사용자가 재고가 1인 상품을 동시에 주문하면 1명은 주문에 성공하고 1명은 주문에 실패한다.")
    @Test
    void order_concurrently() throws InterruptedException {
        long startTime = System.nanoTime();

        int threadCount = 2;
        long initialStock = 1L;
        long price = 1000L;
        long quantity = 1L;

        Product product = productRepository.save(Product.of(
                "product",
                "This is a product",
                price,
                initialStock)
        );

        List<User> users = IntStream.range(0, threadCount)
                .mapToObj(i -> userRepository.save(User.of()))
                .toList();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (User user : users) {
            executor.submit(() -> {
                try {
                    OrderCreateCommand command = OrderCreateCommand.of(
                            user.getId(),
                            null,
                            List.of(OrderProductInfo.of(product.getId(), product.getPrice(), quantity))
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
        executor.shutdown();

        Product deductedProduct = productRepository.findById(product.getId()).orElseThrow();
        List<OrderProduct> orderProducts = orderRepository.findAllOrderProducts();

        long totalOrderedQuantity = orderProducts.stream()
                .filter(op -> op.getProductId().equals(product.getId()))
                .mapToLong(OrderProduct::getQuantity)
                .sum();

        assertThat(failCount.get()).isEqualTo(1);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(deductedProduct.getStock()).isEqualTo(0);
        assertThat(totalOrderedQuantity).isEqualTo(initialStock);

        long endTime = System.nanoTime();
        long durationMillis = (endTime - startTime) / 1_000_000;

        System.out.println("실행 시간: " + durationMillis + " ms");
    }
}
