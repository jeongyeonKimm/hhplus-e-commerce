package kr.hhplus.be.server.interfaces.api.bestseller;

import kr.hhplus.be.server.application.bestseller.BestSellerFacade;
import kr.hhplus.be.server.application.bestseller.dto.BestSellerGetResult;
import kr.hhplus.be.server.domain.bestseller.BestSeller;
import kr.hhplus.be.server.domain.bestseller.dto.BestSellerDto;
import kr.hhplus.be.server.domain.product.Product;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BestSellerController.class)
class BestSellerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BestSellerFacade bestSellerFacade;

    @DisplayName("인기 상품을 조회한다.")
    @Test
    void getBestProducts_success() throws Exception {
        Product product1 = Instancio.of(Product.class)
                .set(field(Product::getId), 1L)
                .create();
        Product product2 = Instancio.of(Product.class)
                .set(field(Product::getId), 2L)
                .create();
        Product product3 = Instancio.of(Product.class)
                .set(field(Product::getId), 3L)
                .create();
        Product product4 = Instancio.of(Product.class)
                .set(field(Product::getId), 4L)
                .create();
        Product product5 = Instancio.of(Product.class)
                .set(field(Product::getId), 5L)
                .create();

        List<BestSeller> bestSellers = List.of(
                BestSeller.of(product1, 500L),
                BestSeller.of(product2, 400L),
                BestSeller.of(product3, 300L),
                BestSeller.of(product4, 200L),
                BestSeller.of(product5, 100L)
        );

        BestSellerGetResult result = BestSellerGetResult.from(BestSellerDto.of(bestSellers));
        given(bestSellerFacade.getBestSellers()).willReturn(result);

        mockMvc.perform(get("/api/v1/bestsellers"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("요청이 정상적으로 처리되었습니다."))
                .andExpect(jsonPath("$.data.products.length()").value(5));
    }
}
