package kr.hhplus.be.server.interfaces.api.point;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointUseRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.response.PointResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointE2ETest {

    RestClient restClient;

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/v1/points";
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @DisplayName("POST /api/v1/points/charge로 양수가 아닌 유저 아이디가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void chargePoint_withNotPositiveUserId(Long userId) {
        PointChargeRequest request = PointChargeRequest.of(userId, 1000L);

        assertThatThrownBy(() -> restClient.post()
                .uri("/charge")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                })
        )
                .isInstanceOf(HttpClientErrorException.class);
    }

    @DisplayName("POST /api/v1/points/charge로 양수가 아닌 충전 금액이 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0, -1000})
    @ParameterizedTest
    void chargePoint_withNotPositiveChargeAmount(Long chargeAmount) {
        PointChargeRequest request = PointChargeRequest.of(1L, chargeAmount);
        assertThatThrownBy(() -> restClient.post()
                .uri("/charge")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {})
        )
                .isInstanceOf(HttpClientErrorException.class);
    }

    @DisplayName("POST /api/v1/points/charge로 1000000을 넘는 충전 금액이 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {1000001, 2000000})
    @ParameterizedTest
    void chargePoint_withExceededChargeAmount(Long chargeAmount) {
        PointChargeRequest request = PointChargeRequest.of(1L, chargeAmount);

        assertThatThrownBy(() -> restClient.post()
                .uri("/charge")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                })
        )
                .isInstanceOf(HttpClientErrorException.class);
    }

    @DisplayName("POST /api/v1/points/charge로 양수의 유저 ID, 1~1000000 범위의 충전 금액과함께 포인트 충전 요청을 보내면 상태 코드 200과 조회된 유저 포인트를 응답한다.")
    @Test
    void chargePoint_success() {
        long balance = 10000L;
        long chargeAmount = 10000L;
        User user = userRepository.save(User.of());
        pointRepository.savePoint(Point.of(user.getId(), balance));

        PointChargeRequest request = PointChargeRequest.of(user.getId(), chargeAmount);

        ApiResponse<PointResponse> response = restClient.post()
                .uri("/charge")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<PointResponse>>() {
                })
                .getBody();

        long expected = balance + chargeAmount;
        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData())
                .extracting("userId", "balance")
                .contains(user.getId(), expected);
    }

    @DisplayName("GET /api/v1/points?userId={userId}로 양수가 아닌 유저 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void getPoint_withNotPositiveUserId(Long userId) throws JsonProcessingException {
        assertThatThrownBy(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {})
        )
                .isInstanceOf(HttpClientErrorException.class);
    }

    @DisplayName("GET /api/v1/points?userId={userId}로 양수의 유저 ID와 함께 포인트 조회 요청을 보내면 상태 코드 200과 조회된 유저 포인트를 응답한다.")
    @Test
    void getPoint_success() {
        long balance = 10000L;
        User user = userRepository.save(User.of());
        pointRepository.savePoint(Point.of(user.getId(), balance));

        ApiResponse<PointResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("userId", user.getId())
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<PointResponse>>() {})
                .getBody();

        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData())
                .extracting("userId", "balance")
                .contains(user.getId(), balance);
    }

    @DisplayName("POST /api/v1/points/use로 음수의 주문 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void usePoint_withNotPositiveOrderId(Long orderId) {
        PointUseRequest request = PointUseRequest.of(orderId);

        assertThatThrownBy(() -> restClient.post()
                .uri("/use")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {})
        )
                .isInstanceOf(HttpClientErrorException.class);
    }

    @DisplayName("POST /api/v1/points/use로 양수의 주문 아이디와 함께 포인트 사용 요청을 보내면 상태 코드 204과 조회된 유저 포인트를 응답한다.")
    @Test
    void usePoint_success() {
        User user = userRepository.save(User.of());
        pointRepository.savePoint(Point.of(user.getId(), 10000L));

        Product product = productRepository.save(Product.of("123", "1234123123", 1000L, 100L));
        Order order = orderRepository.saveOrder(Order.of(user.getId()));
        OrderProduct orderProduct = OrderProduct.of(order, product, 1L);
        order.addProduct(product, orderProduct);

        orderRepository.saveOrder(order);
        orderRepository.saveAllOrderProducts(order.getOrderProducts());

        PointUseRequest request = PointUseRequest.of(order.getId());

        ApiResponse<PointResponse> response = restClient.post()
                .uri("/use")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<PointResponse>>() {
                })
                .getBody();

        assertThat(response.getCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
