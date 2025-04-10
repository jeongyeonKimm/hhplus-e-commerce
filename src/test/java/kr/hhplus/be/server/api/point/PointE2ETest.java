package kr.hhplus.be.server.api.point;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointUseRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.response.PointResponse;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointE2ETest {

    RestClient restClient;

    @LocalServerPort
    private int port;

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
    void chargePoint_withNotPositiveUserId(Long userId) throws JsonProcessingException {
        try {
            PointChargeRequest request = PointChargeRequest.builder()
                    .userId(userId)
                    .chargeAmount(1000)
                    .build();

            restClient.post()
                    .uri("/charge")
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/points/charge로 양수가 아닌 충전 금액이 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(ints = {0, -1000})
    @ParameterizedTest
    void chargePoint_withNotPositiveChargeAmount(Integer chargeAmount) throws JsonProcessingException {
        try {
            PointChargeRequest request = PointChargeRequest.builder()
                    .userId(1L)
                    .chargeAmount(chargeAmount)
                    .build();

            restClient.post()
                    .uri("/charge")
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/points/charge로 1000000을 넘는 충전 금액이 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(ints = {1000001, 2000000})
    @ParameterizedTest
    void chargePoint_withExceededChargeAmount(Integer chargeAmount) throws JsonProcessingException {
        try {
            PointChargeRequest request = PointChargeRequest.builder()
                    .userId(1L)
                    .chargeAmount(chargeAmount)
                    .build();

            restClient.post()
                    .uri("/charge")
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/points/charge로 양수의 유저 ID, 1~1000000 범위의 충전 금액과함께 포인트 충전 요청을 보내면 상태 코드 200과 조회된 유저 포인트를 응답한다.")
    @Test
    void chargePoint_success() {
        PointChargeRequest request = PointChargeRequest.builder()
                .userId(1L)
                .chargeAmount(10000)
                .build();

        ApiResponse<PointResponse> response = restClient.post()
                .uri("/charge")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<PointResponse>>() {})
                .getBody();

        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData())
                .extracting("userId", "balance")
                .contains(1L, 11000);
    }

    @DisplayName("GET /api/v1/points?userId={userId}로 양수가 아닌 유저 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void getPoint_withNotPositiveUserId(Long userId) throws JsonProcessingException {
        try {
            restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("userId", userId)
                            .build())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("GET /api/v1/points?userId={userId}로 양수의 유저 ID와 함께 포인트 조회 요청을 보내면 상태 코드 200과 조회된 유저 포인트를 응답한다.")
    @Test
    void getPoint_success() {
        Long userId = 1L;

        ApiResponse<PointResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("userId", userId)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<PointResponse>>() {})
                .getBody();

        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData())
                .extracting("userId", "balance")
                .contains(userId, 2000);
    }

    @DisplayName("POST /api/v1/points/use로 음수의 주문 ID가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void usePoint_withNotPositiveOrderId(Long orderId) throws JsonProcessingException {
        try {
            PointUseRequest request = PointUseRequest.of(orderId);

            restClient.post()
                    .uri("/use")
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/points/use로 양수의 주문 아이디와 함께 포인트 사용 요청을 보내면 상태 코드 204과 조회된 유저 포인트를 응답한다.")
    @Test
    void usePoint_success() {
        PointUseRequest request = PointUseRequest.of(1L);

        ApiResponse<PointResponse> response = restClient.post()
                .uri("/use")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<PointResponse>>() {})
                .getBody();

        assertThat(response.getCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
