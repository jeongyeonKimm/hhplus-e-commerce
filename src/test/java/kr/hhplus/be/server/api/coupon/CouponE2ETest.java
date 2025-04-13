package kr.hhplus.be.server.api.coupon;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.coupon.dto.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.dto.response.CouponListResponse;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CouponE2ETest {

    RestClient restClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/v1/coupons";
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @DisplayName("GET /api/v1/coupons?userId={userId}로 양수가 아닌 유저 아이디가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void getCoupons_withNotPositiveUserId(Long userId) throws JsonProcessingException {
        try {
            restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("userId", userId)
                            .build())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("GET /api/v1/coupons?userId={userId}로 양수의 유저 ID와 함께 쿠폰 조회 요청을 보내면 상태 코드 200과 조회된 유저 쿠폰을 응답한다.")
    @Test
    void getCoupons_success() {
        ApiResponse<CouponListResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("userId", 1L)
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<CouponListResponse>>() {})
                .getBody();

        assertThat(response.getCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getUserId()).isEqualTo(1L);
        assertThat(response.getData().getCoupons())
                .extracting("id", "title", "discountType", "discountValue", "startDate", "endDate")
                .containsExactlyInAnyOrder(
                        tuple(2L, "10% 할인", "RATE", 10, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 5)),
                        tuple(3L, "1000원 할인", "AMOUNT", 1000, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 5))
                );
    }

    @DisplayName("POST /api/v1/coupons/issue로 양수가 아닌 유저 아이디가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void issueCoupon_withNotPositiveUserId(Long userId) throws JsonProcessingException {
        try {
            CouponIssueRequest request = CouponIssueRequest.of(userId, 1L);

            restClient.post()
                    .uri("/issue")
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("GET /api/v1/coupons/issue로 양수가 아닌 쿠폰 아이디가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void issueCoupon_withNotPositiveCouponId(Long couponId) throws JsonProcessingException {
        try {
            CouponIssueRequest request = CouponIssueRequest.of(1L, couponId);

            restClient.post()
                    .uri("/issue")
                    .body(request)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @DisplayName("POST /api/v1/coupons/issue로 양수의 유저 ID와 함께 쿠폰 발급 요청을 보내면 상태 코드 201를 응답한다.")
    @Test
    void issueCoupon_success() {
        CouponIssueRequest request = CouponIssueRequest.of(1L, 2L);

        ApiResponse<List<?>> response = restClient.post()
                .uri("/issue")
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<List<?>>>() {})
                .getBody();

        assertThat(response.getCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getData()).isNotNull();
    }
}
