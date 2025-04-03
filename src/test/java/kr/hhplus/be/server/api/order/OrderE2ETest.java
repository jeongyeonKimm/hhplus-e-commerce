package kr.hhplus.be.server.api.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.api.order.dto.request.OrderProductRequest;
import kr.hhplus.be.server.api.order.dto.request.OrderRequest;
import kr.hhplus.be.server.api.order.dto.response.OrderResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderE2ETest {

    RestClient restClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/v1/orders";
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @DisplayName("POST /api/v1/orders로 양수가 아닌 유저 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void createOrder_withNotPositiveUserId(Long userId) throws JsonProcessingException {
        try {
            OrderProductRequest orderProduct = OrderProductRequest.builder()
                    .productId(1L)
                    .quantity(1)
                    .build();

            OrderRequest request = OrderRequest.builder()
                    .userId(userId)
                    .userCouponId(2L)
                    .orderProducts(List.of(orderProduct))
                    .build();

            restClient.post()
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/orders로 양수가 아닌 유저 쿠폰 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void createOrder_withNotPositiveUserCouponId(Long userCouponId) throws JsonProcessingException {
        try {
            OrderProductRequest orderProduct = OrderProductRequest.builder()
                    .productId(1L)
                    .quantity(1)
                    .build();

            OrderRequest request = OrderRequest.builder()
                    .userId(2L)
                    .userCouponId(userCouponId)
                    .orderProducts(List.of(orderProduct))
                    .build();

            restClient.post()
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/orders로 주문 상품이 없이 들어오면 상태 코드 400을 응답한다.")
    @Test
    void createOrder_withNoOrderProduct() throws JsonProcessingException {
        try {
            OrderRequest request = OrderRequest.builder()
                    .userId(2L)
                    .userCouponId(3L)
                    .orderProducts(List.of())
                    .build();

            restClient.post()
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/orders로 양수가 아닌 상품 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void createOrder_withNotPositiveProductId(Long productId) throws JsonProcessingException {
        try {
            OrderProductRequest orderProduct = OrderProductRequest.builder()
                    .productId(productId)
                    .quantity(1)
                    .build();

            OrderRequest request = OrderRequest.builder()
                    .userId(2L)
                    .userCouponId(3L)
                    .orderProducts(List.of(orderProduct))
                    .build();

            restClient.post()
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/orders로 1보다 작은 주문 상품 수량이 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(ints = {0, -100})
    @ParameterizedTest
    void createOrder_withLessThanOneQuantity(Integer quantity) throws JsonProcessingException {
        try {
            OrderProductRequest orderProduct = OrderProductRequest.builder()
                    .productId(1L)
                    .quantity(quantity)
                    .build();

            OrderRequest request = OrderRequest.builder()
                    .userId(2L)
                    .userCouponId(3L)
                    .orderProducts(List.of(orderProduct))
                    .build();

            restClient.post()
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/orders로 양수인 유저 ID, 유저 쿠폰 ID와 주문 상품이 1개 이상이면서 양수인 주문 ID, 수량이 들어오면 상태 코드 201을 응답한다.")
    @Test
    void createOrder_success() throws JsonProcessingException {
        OrderProductRequest orderProduct = OrderProductRequest.builder()
                .productId(1L)
                .quantity(10)
                .build();

        OrderRequest request = OrderRequest.builder()
                .userId(2L)
                .userCouponId(3L)
                .orderProducts(List.of(orderProduct))
                .build();

        ApiResponse<OrderResponse> response = restClient.post()
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<OrderResponse>>() {})
                .getBody();

        assertThat(response.getCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getOrderId()).isEqualTo(10L);
    }
}
