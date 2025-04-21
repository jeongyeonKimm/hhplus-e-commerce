package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @DisplayName("전체 상품 목록을 조회한다.")
    @Test
    void getProduct() {
        List<Product> products = List.of(
                Product.of(
                        "iPhone 15",
                        "Apple iPhone15".getBytes(),
                        1_000_000L,
                        100L
                ),
                Product.of(
                        "iPad Gen5",
                        "Apple iPad Gen5".getBytes(),
                        1_000_000L,
                        200L
                )
        );

        given(productRepository.findAll()).willReturn(products);

        List<ProductResult> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("name", "price", "stock")
                .containsExactlyInAnyOrder(
                        tuple("iPhone 15", 1_000_000L, 100L),
                        tuple("iPad Gen5", 1_000_000L, 200L)
                );

        verify(productRepository, times(1)).findAll();
    }
}
