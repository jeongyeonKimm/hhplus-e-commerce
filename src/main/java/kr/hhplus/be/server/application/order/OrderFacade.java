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

import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderFacade {

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;

    public OrderResult order(OrderCreateCommand command) {
        Order order = orderService.createOrder(command.getUserId());

        List<OrderProductInfo> productInfos = command.getProductInfos();
        for (OrderProductInfo productInfo : productInfos) {
            Product product = productService.getProduct(productInfo.getProductId());
            orderService.addProduct(order, product, productInfo.getQuantity());
        }

        if (command.getUserCouponId() != null) {
            UserCoupon userCoupon = couponService.getUserCoupon(command.getUserId());
            orderService.applyCoupon(order, userCoupon);
        }

        orderService.saveOrder(order);
        return OrderResult.from(order);
    }
}
