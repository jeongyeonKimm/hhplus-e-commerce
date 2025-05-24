package kr.hhplus.be.server.interfaces.api.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.dto.OrderCreateCommand;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.api.order.dto.request.OrderProductRequest;
import kr.hhplus.be.server.interfaces.api.order.dto.request.OrderRequest;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrderController.class)
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderFacade orderFacade;

    @DisplayName("주문을 생성한다.")
    @Test
    void createOrder_success() throws Exception {
        User user = Instancio.of(User.class)
                .set(field(User::getId), 1L)
                .create();
        UserCoupon userCoupon = Instancio.of(UserCoupon.class)
                .set(field(UserCoupon::getId), 3L)
                .set(field(UserCoupon::getUserId), user.getId())
                .create();
        Product product = Instancio.of(Product.class)
                .set(field(Product::getId), 2L)
                .create();
        List<OrderProductRequest> orderProducts = List.of(
                OrderProductRequest.of(product.getId(), 10L)
        );

        OrderRequest request = OrderRequest.of(user.getId(), userCoupon.getId(), orderProducts);

        Order order = Instancio.of(Order.class).create();
        OrderResult result = OrderResult.from(order);
        given(orderFacade.order(any(OrderCreateCommand.class))).willReturn(result);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("요청이 정상적으로 처리되었습니다."));
    }
}
