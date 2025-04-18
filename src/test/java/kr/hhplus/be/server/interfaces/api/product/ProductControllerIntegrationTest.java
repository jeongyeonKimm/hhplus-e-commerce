package kr.hhplus.be.server.interfaces.api.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.ProductResult;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProductController.class)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @DisplayName("전체 상품 목록을 조회한다.")
    @Test
    void getProducts_success() throws Exception {
        Product product1 = Instancio.of(Product.class).create();
        Product product2 = Instancio.of(Product.class).create();
        Product product3 = Instancio.of(Product.class).create();

        List<ProductResult> results = List.of(
                ProductResult.from(product1),
                ProductResult.from(product2),
                ProductResult.from(product3)
        );

        given(productService.getAllProducts()).willReturn(results);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("요청이 정상적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.products.length()").value(3));
    }
}
