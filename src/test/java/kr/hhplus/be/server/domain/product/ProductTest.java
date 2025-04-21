package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.common.exception.ApiException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static kr.hhplus.be.server.common.exception.ErrorCode.INVALID_RESTORE_QUANTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

class ProductTest {

    @DisplayName("재고 이하의 수량을 차감하려 하면 재고가 정상 차감된다.")
    @Test
    void deduct_success() {
        long stock = 100L;
        long orderQuantity = 50L;

        Product product = Product.of(
                "iPhone 15",
                "Apple iPhone15",
                1_000_000L,
                stock
        );

        product.deduct(orderQuantity);

        long expectedStock = stock - orderQuantity;
        assertThat(product.getStock()).isEqualTo(expectedStock);
    }

    @DisplayName("재고 보다 많은 수량을 차감하려 하면 재고 차감에 실패하고 InsufficientStockException이 발생한다.")
    @ValueSource(longs = {101L, 200L})
    @ParameterizedTest
    void deduct_shouldThrowInsufficientStockException_whenOrderExceededStock(long orderQuantity) {
        long stock = 100;

        Product product = Product.of(
                "iPhone 15",
                "Apple iPhone15",
                1_000_000L,
                stock
        );

        assertThatThrownBy(() -> product.deduct(orderQuantity))
                .isInstanceOf(ApiException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_STOCK.getMessage());
    }

    @DisplayName("0 이하의 재고를 복원하려는 경우 재고 복원에 실패하고 InvalidRestoreQuantityException이 발생한다.")
    @ValueSource(longs = {0, -1})
    @ParameterizedTest
    void restore_throwInvalidRestoreQuantity_whenRestoreQuantityIsLessThanOrEqualZero(long quantity) {
        Product product = Instancio.of(Product.class)
                .set(field("stock"), 10L)
                .create();

        assertThatThrownBy(() -> product.restore(quantity))
                .isInstanceOf(ApiException.class)
                .hasMessage(INVALID_RESTORE_QUANTITY.getMessage());
    }

    @DisplayName("재고를 복원하면 기존 재고에 복원되는 수량이 더해진다.")
    @Test
    void restore_success() {
        Product product = Instancio.of(Product.class)
                .set(field("stock"), 10L)
                .create();

        product.restore(2L);

        assertThat(product.getStock()).isEqualTo(12);
    }

}
