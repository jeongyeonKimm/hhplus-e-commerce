package kr.hhplus.be.server.api.coupon;

import kr.hhplus.be.server.api.coupon.dto.request.CouponIssueRequest;
import kr.hhplus.be.server.api.coupon.dto.response.CouponListResponse;
import kr.hhplus.be.server.api.coupon.dto.response.CouponResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class CouponMockController {

    @GetMapping("/api/v1/coupons")
    public ApiResponse<CouponListResponse> getCoupons(@RequestParam Long userId) {
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

    @PostMapping("/api/v1/coupons/issue")
    public ApiResponse<List<?>> issueCoupon(@RequestBody CouponIssueRequest request) {
        return ApiResponse.successWithCreated(List.of());
    }
}
