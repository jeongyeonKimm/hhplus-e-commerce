package kr.hhplus.be.server.api.point;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.api.point.api.PointApi;
import kr.hhplus.be.server.api.point.dto.request.PointChargeRequest;
import kr.hhplus.be.server.api.point.dto.request.PointUseRequest;
import kr.hhplus.be.server.api.point.dto.response.PointResponse;
import kr.hhplus.be.server.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class PointMockController implements PointApi {

    @PostMapping("/api/v1/points/charge")
    public ApiResponse<PointResponse> chargePoint(@Valid @RequestBody PointChargeRequest request) {
        int newAmount = 1000 + request.getChargeAmount();

        PointResponse response = PointResponse.builder()
                .userId(request.getUserId())
                .balance(newAmount)
                .build();

        return ApiResponse.success(response);
    }

    @GetMapping("/api/v1/points")
    public ApiResponse<PointResponse> getPoint(@Positive @RequestParam Long userId) {
        PointResponse response = PointResponse.builder()
                .userId(userId)
                .balance(2000)
                .build();

        return ApiResponse.success(response);
    }

    @PostMapping("/api/v1/points/use")
    public ApiResponse<PointResponse> usePoint(@Valid @RequestBody PointUseRequest request) {
        return ApiResponse.successWithNoContent();
    }
}
