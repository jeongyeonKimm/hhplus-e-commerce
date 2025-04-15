package kr.hhplus.be.server.interfaces.api.coupon;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.interfaces.api.coupon.dto.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.dto.response.CouponListResponse;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CouponE2ETest {

    RestClient restClient;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired private CouponRepository couponRepository;

    @Autowired private UserCouponRepository userCouponRepository;

    @Autowired private UserRepository userRepository;

    @Autowired private CouponFacade couponFacade;

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
        User user = userRepository.save(User.of());

        Coupon coupon1 = couponRepository.save(Coupon.of(
                "10% 할인", 10L, DiscountType.RATE,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 5),
                100L
        ));
        Coupon coupon2 = couponRepository.save(Coupon.of(
                "1000원 할인",  1000L, DiscountType.AMOUNT,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 5),
                50L
        ));

        userCouponRepository.save(UserCoupon.of(user, coupon1));
        userCouponRepository.save(UserCoupon.of(user, coupon2));

        ApiResponse<CouponListResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("userId", user.getId())
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        System.out.println(response);

        CouponListResponse data = response.getData();
        assertThat(data.getUserId()).isEqualTo(user.getId());
        assertThat(data.getCoupons())
                .extracting("title", "discountType", "discountValue", "startDate", "endDate")
                .containsExactlyInAnyOrder(
                        tuple("10% 할인", DiscountType.RATE, 10L, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 5)),
                        tuple("1000원 할인", DiscountType.AMOUNT, 1000L, LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 5))
                );
    }

    @DisplayName("POST /api/v1/coupons/issue로 양수가 아닌 유저 아이디가 들어오면 상태 코드 400을 응답한다.")
    @ValueSource(longs = {0L, -1000L})
    @ParameterizedTest
    void issueCoupon_withNotPositiveUserId(Long userId) {
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
    void issueCoupon_withNotPositiveCouponId(Long couponId) {
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
        User user = userRepository.save(User.of());

        Coupon coupon = couponRepository.save(Coupon.of(
                "10% 할인", 10L, DiscountType.RATE,
                LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 5),
                100L
        ));

        CouponIssueRequest request = CouponIssueRequest.of(user.getId(), coupon.getId());

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
