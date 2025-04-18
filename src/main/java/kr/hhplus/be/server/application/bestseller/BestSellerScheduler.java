package kr.hhplus.be.server.application.bestseller;

import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.BestSellerService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class BestSellerScheduler {

    private final OrderService orderService;
    private final ProductService productService;
    private final BestSellerService bestSellerService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void calculateHourlyBestSellers() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);

        List<Order> orders = orderService.findPaidOrdersBetween(oneHourAgo, now);

        Map<Long, Long> productIdTotalSales = orders.stream()
                .flatMap(order -> order.getOrderProducts().stream())
                .collect(Collectors.groupingBy(
                        OrderProduct::getProductId,
                        Collectors.summingLong(OrderProduct::getQuantity)
                ));

        for (Map.Entry<Long, Long> entry : productIdTotalSales.entrySet()) {
            Long productId = entry.getKey();
            Long sales = entry.getValue();

            Product product = productService.getProduct(productId);
            BestSeller bestSeller = BestSeller.of(product, sales);
            bestSellerService.save(bestSeller);
        }
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void deleteOldBestSellers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(3);
        bestSellerService.deleteByCreatedAtBefore(threshold);
    }
}
