package kr.hhplus.be.server.interfaces.api.coupon;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.dto.CouponGetResult;
import kr.hhplus.be.server.application.coupon.dto.command.CouponGetCommand;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.coupon.dto.request.CouponIssueRequest;
import kr.hhplus.be.server.interfaces.api.coupon.dto.response.CouponListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@RestController
public class CouponController implements CouponSpec {

    private final CouponFacade couponFacade;

    @GetMapping
    public ApiResponse<CouponListResponse> getCoupons(@Positive @RequestParam Long userId) {
        CouponGetResult result = couponFacade.getCoupons(CouponGetCommand.of(userId));
        return ApiResponse.success(CouponListResponse.from(result));
    }

    @PostMapping("/issue")
    public ApiResponse<List<?>> issueCoupon(@Valid @RequestBody CouponIssueRequest request) {
        couponFacade.issueCoupon(request.toCouponIssueCommand());
        return ApiResponse.successWithCreated(List.of());
    }

    @PostMapping("/issue/request")
    public ApiResponse<String> requestIssueCoupon(@Valid @RequestBody CouponIssueRequest request) {
        boolean result = couponFacade.requestIssueCoupon(request.toCouponIssueCommand());
        return result
                ? ApiResponse.success("쿠폰 발급 요청이 정상 처리되었습니다.")
                : ApiResponse.error(400, "쿠폰 발급 요청에 실패하였습니다.");
    }
}
