package kr.hhplus.be.server.interfaces.api.coupon;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.coupon.dto.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.interfaces.api.coupon.dto.response.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@RestController
public class CouponController implements CouponSpec {

    private final CouponFacade couponFacade;

    @GetMapping
    public ApiResponse<CouponListResponse> getCoupons(@Positive @RequestParam Long userId) {
        List<CouponResponse> coupons = List.of(
                CouponResponse.builder()
                        .id(2L)
                        .title("10% 할인")
                        .discountType("RATE")
                        .discountValue(10)
                        .startDate(LocalDate.of(2025, 4, 1))
                        .endDate(LocalDate.of(2025, 4, 5))
                        .build(),
                CouponResponse.builder()
                        .id(3L)
                        .title("1000원 할인")
                        .discountType("AMOUNT")
                        .discountValue(1000)
                        .startDate(LocalDate.of(2025, 4, 1))
                        .endDate(LocalDate.of(2025, 4, 5))
                        .build()
        );

        CouponListResponse response = CouponListResponse.builder()
                .userId(userId)
                .coupons(coupons)
                .build();

        return ApiResponse.success(response);
    }

    @PostMapping("/issue")
    public ApiResponse<List<?>> issueCoupon(@Valid @RequestBody CouponIssueRequest request) {
        couponFacade.issueCoupon(request.toCouponIssueCommand());
        return ApiResponse.successWithCreated(List.of());
    }
}
