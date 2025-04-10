package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @DisplayName("재고 이하의 수량을 차감하려 하면 재고가 정상 차감된다.")
    @Test
    void deduct_success() {
        int stock = 100;
        int orderQuantity = 50;

        Product product = Product.builder()
                .id(1L)
                .name("iPhone 15")
                .description("Apple iPhone15".getBytes())
                .price(1_000_000)
                .stock(stock)
                .build();

        product.deduct(orderQuantity);

        int expectedStock = stock - orderQuantity;
        assertThat(product.getStock()).isEqualTo(expectedStock);
    }

    @DisplayName("재고 보다 많은 수량을 차감하려 하면 재고 차감에 실패하고 InsufficientStockException이 발생한다.")
    @ValueSource(ints = {101, 200})
    @ParameterizedTest
    void deduct_shouldThrowInsufficientStockException_whenOrderExceededStock(int orderQuantity) {
        int stock = 100;

        Product product = Product.builder()
                .id(1L)
                .name("iPhone 15")
                .description("Apple iPhone15".getBytes())
                .price(1_000_000)
                .stock(stock)
                .build();

        assertThatThrownBy(() -> product.deduct(orderQuantity))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

}
