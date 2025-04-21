package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.DiscountType;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.product.Product;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static kr.hhplus.be.server.common.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class OrderTest {

    @DisplayName("주문에 상품을 추가하면 orderProducts에 상품이 추가되고 totalAmount에 추가한 상품의 가격 * 수량만큼 누적된다.")
    @Test
    void addProduct_success() {
        long price = 1000L;
        long initialStock = 1000L;
        Product product = Instancio.of(Product.class)
                .set(field("price"), price)
                .set(field("stock"), initialStock)
                .create();

        Order order = Instancio.of(Order.class)
                .set(field("orderProducts"), new ArrayList<>())
                .set(field("totalAmount"), 0L)
                .create();

        long orderQuantity = 10;
        OrderProduct orderProduct = Instancio.of(OrderProduct.class)
                .set(field("orderId"), order.getId())
                .set(field("productId"), product.getId())
                .set(field("price"), product.getPrice())
                .set(field("quantity"), orderQuantity)
                .create();

        order.addProduct(product, orderProduct);

        long expectedStock = initialStock - orderQuantity;
        long expectedTotalAmount = orderQuantity * price;
        assertThat(product.getStock()).isEqualTo(expectedStock);
        assertThat(order.getTotalAmount()).isEqualTo(expectedTotalAmount);
        assertThat(order.getOrderProducts()).hasSize(1);
    }

    @DisplayName("이미 쿠폰이 적용된 주문의 경우 쿠폰 적용에 실패하고 AlreadyUsedCouponException이 발생한다.")
    @Test
    void applyCoupon_throwCouponAlreadyApplied_whenCouponIsApplied() {
        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .create();

        Order order = Instancio.of(Order.class)
                .set(field("isCouponApplied"), true)
                .create();

        assertThatThrownBy(() -> order.applyCoupon(userCoupon))
                .isInstanceOf(ApiException.class)
                .hasMessage(COUPON_ALREADY_APPLIED.getMessage());
    }

    @DisplayName("이용 불가능한 쿠폰을 주문에 적용하려 하면 경우 쿠폰 적용에 실패하고 AlreadyUsedCouponException이 발생한다.")
    @Test
    void applyCoupon_throwInvalidCoupon_whenCouponIsUsed() {
        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field("isUsed"), true)
                .create();

        Order order = Instancio.of(Order.class)
                .set(field("isCouponApplied"), false)
                .create();

        assertThatThrownBy(() -> order.applyCoupon(userCoupon))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_COUPON.getMessage());
    }

    @DisplayName("쿠폰이 적용되지 않은 주문에 유효한 쿠폰을 적용하면 주문의 totalAmount가 쿠폰이 적용된 금액으로 바뀌고, 쿠폰은 사용 처리 된다.")
    @Test
    void applyCoupon_success() {
        long discountValue = 1000;
        Coupon coupon = Instancio.of(Coupon.class)
                .set(field("discountType"), DiscountType.AMOUNT)
                .set(field("discountValue"), discountValue)
                .set(field("endDate"), LocalDate.now().plusDays(3))
                .create();

        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field("isUsed"), false)
                .set(field("coupon"), coupon)
                .create();

        long totalAmount = 10000;
        Order order = Instancio.of(Order.class)
                .set(field("isCouponApplied"), false)
                .set(field("totalAmount"), totalAmount)
                .create();

        order.applyCoupon(userCoupon);

        long expectedAmount = totalAmount - discountValue;
        assertThat(order.getIsCouponApplied()).isTrue();
        assertThat(order.getTotalAmount()).isEqualTo(expectedAmount);
        assertThat(userCoupon.getIsUsed()).isTrue();

    }

    @DisplayName("주문 상태가 NOT_PAID 상태가 아니면 주문 상태 업데이트가 불가하고 OrderPaymentInvalidStateException이 발생한다.")
    @Test
    void pay_throwOrderPaymentInvalidState_whenOrderStateIsNotNOT_PAID() {
        Order order = Instancio.of(Order.class)
                .set(field("status"), OrderStatus.EXPIRED)
                .create();

        assertThatThrownBy(order::pay)
                .isInstanceOf(ApiException.class)
                .hasMessage(ORDER_PAYMENT_INVALID_STATE.getMessage());
    }

    @DisplayName("주문 상태가 NOT_PAID 상태에서 결제가 진행되면 PAID 상태로 바뀐다.")
    @Test
    void pay_success() {
        Order order = Instancio.of(Order.class)
                .set(field("status"), OrderStatus.NOT_PAID)
                .create();

        order.pay();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @DisplayName("만료된 주문은 상태를 EXPIRED로 바꾸고 재고를 복원한다.")
    @Test
    void expired_success() {
        long productId = 1L;
        Product product = Instancio.of(Product.class)
                .set(field("id"), productId)
                .create();

        OrderProduct orderProduct = mock(OrderProduct.class);
        given(orderProduct.getProductId()).willReturn(productId);

        Order order = Instancio.of(Order.class)
                .set(field("orderProducts"), List.of(orderProduct))
                .create();

        order.expired(List.of(product));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.EXPIRED);
        verify(orderProduct, times(1)).restoreStock(product);
    }

}
