package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.product.dto.ProductResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
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
                Product.create(1L, "iPhone 13", "Apple iPhone 13", 1_000_000, 100),
                Product.create(2L, "iPad Gen5", "Apple iPad Gen5", 1_500_000, 200)
        );

        given(productRepository.findAll()).willReturn(products);

        List<ProductResult> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting("id", "name", "price", "stock")
                .containsExactlyInAnyOrder(
                        tuple(1L, "iPhone 13", 1_000_000, 100),
                        tuple(2L, "iPad Gen5", 1_500_000, 200)
                );
    }
}
