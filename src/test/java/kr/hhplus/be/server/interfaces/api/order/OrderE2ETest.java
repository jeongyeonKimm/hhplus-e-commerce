package kr.hhplus.be.server.interfaces.api.order;

import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.order.dto.request.OrderProductRequest;
import kr.hhplus.be.server.interfaces.api.order.dto.request.OrderRequest;
import kr.hhplus.be.server.interfaces.api.order.dto.response.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderE2ETest {

    @LocalServerPort
    private int port;

    @Autowired private TestRestTemplate testRestTemplate;

    @Autowired private UserRepository userRepository;

    @Autowired private ProductRepository productRepository;

    @Autowired private CouponRepository couponRepository;

    @Autowired private UserCouponRepository userCouponRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/orders";
    }

    @DisplayName("POST /api/v1/orders로 양수가 아닌 유저 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void createOrder_withNotPositiveUserId(Long userId) {
        OrderProductRequest orderProduct = OrderProductRequest.of(1L, 1L);
        OrderRequest request = OrderRequest.of(userId, 2L, List.of(orderProduct));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("POST /api/v1/orders로 양수가 아닌 유저 쿠폰 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void createOrder_withNotPositiveUserCouponId(Long userCouponId) {
        OrderProductRequest orderProduct = OrderProductRequest.of(1L, 1L);
        OrderRequest request = OrderRequest.of(2L, userCouponId, List.of(orderProduct));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("POST /api/v1/orders로 주문 상품이 없이 들어오면 상태 코드 400을 응답한다.")
    @Test
    void createOrder_withNoOrderProduct() {
        OrderRequest request = OrderRequest.of(2L, 3L, List.of());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("POST /api/v1/orders로 양수가 아닌 상품 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void createOrder_withNotPositiveProductId(Long productId) {
        OrderProductRequest orderProduct = OrderProductRequest.of(productId, 1L);
        OrderRequest request = OrderRequest.of(2L, 3L, List.of(orderProduct));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("POST /api/v1/orders로 1보다 작은 주문 상품 수량이 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0, -100})
    @ParameterizedTest
    void createOrder_withLessThanOneQuantity(Long quantity) {
        OrderProductRequest orderProduct = OrderProductRequest.of(1L, quantity);
        OrderRequest request = OrderRequest.of(2L, 3L, List.of(orderProduct));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<>() {}
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("POST /api/v1/orders로 양수인 유저 ID, 유저 쿠폰 ID와 주문 상품이 1개 이상이면서 양수인 주문 ID, 수량이 들어오면 상태 코드 201을 응답한다.")
    @Test
    void createOrder_success() {
        Coupon coupon = couponRepository.save(Coupon.of("coupon", 1000L, DiscountType.AMOUNT, LocalDate.now(), LocalDate.now().plusMonths(1), 100L));
        Product product = productRepository.save(Product.of("123", "1234123123", 1000L, 100L));
        User user = userRepository.save(User.of());
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.of(user, coupon));

        OrderProductRequest orderProduct = OrderProductRequest.of(product.getId(), 10L);
        OrderRequest request = OrderRequest.of(user.getId(), userCoupon.getId(), List.of(orderProduct));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiResponse<OrderResponse>> responseEntity = testRestTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<OrderResponse>>() {}
        );

        assertThat(responseEntity.getBody().getCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
