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
import org.springframework.context.ApplicationContext;
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
    private final ApplicationContext applicationContext;

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

            Product product = productService.getProductWithLock(productId);
            BestSeller bestSeller = BestSeller.of(product, sales);
            bestSellerService.save(bestSeller);
        }

        log.info("[BestSellerScheduler] 1시간 동안 주문된 상품 통계 집계 완료");
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void deleteOldBestSellers() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(2);
        bestSellerService.deleteByCreatedAtBefore(threshold);
        log.info("[BestSellerScheduler] 3일 지난 인기 상품 데이터 삭제 스케줄러 실행 완료");
    }

    @Scheduled(cron = "0 50 23 * * *")
    public void preloadBestSellersCache() {
        BestSellerService proxy = applicationContext.getBean(BestSellerService.class);
        proxy.refreshBestSellers();
        log.info("[BestSellerScheduler] 3일간 인기 상품 캐싱 완료");
    }
}
