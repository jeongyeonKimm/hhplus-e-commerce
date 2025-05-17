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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OrderFacade {

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final SalesRankingCommitHandler salesRankingCommitHandler;

    @Transactional
    public OrderResult order(OrderCreateCommand command) {
        Order order = orderService.createOrder(command.getUserId());

        List<OrderProductInfo> productInfos = command.getProductInfos().stream()
                .sorted(Comparator.comparing(OrderProductInfo::getProductId))
                .toList();

        Map<Long, Long> productSales = new HashMap<>();

        for (OrderProductInfo productInfo : productInfos) {
            Product product = productService.getProductWithLock(productInfo.getProductId());
            orderService.addProduct(order, product, productInfo.getQuantity());
            productSales.put(product.getId(), productInfo.getQuantity());
        }

        if (command.getUserCouponId() != null) {
            UserCoupon userCoupon = couponService.getUserCoupon(command.getUserCouponId());
            orderService.applyCoupon(order, userCoupon);
        }

        orderService.saveOrder(order);
        salesRankingCommitHandler.handlerAfterOrderCommit(productSales);

        return OrderResult.from(order);
    }
}
