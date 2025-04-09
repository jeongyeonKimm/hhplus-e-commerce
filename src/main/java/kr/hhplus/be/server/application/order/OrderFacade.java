package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderProductInfo;
import kr.hhplus.be.server.application.order.dto.OrderProductList;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderFacade {

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;

    public OrderResult order(OrderCreateCommand command) {
        OrderProductList orderProducts = command.getOrderProducts();
        for (OrderProductInfo productInfo : orderProducts.getProductInfos()) {
            productService.deductStock(productInfo.getProductId(), productInfo.getQuantity());
        }

        boolean isCouponApplied = couponService.redeemCoupon(command.getUserCouponId());

        int totalAmount = orderProducts.calculateTotalAmount();
        int finalAmount = couponService.calculateFinalAmount(command.getUserCouponId(), totalAmount);

        Order order = orderService.createOrder(command.toOrder(isCouponApplied, finalAmount), orderProducts.toOrderProducts());
        return OrderResult.from(order);
    }
}
