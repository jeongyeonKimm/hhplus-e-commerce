package kr.hhplus.be.server.interfaces.api.point;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.dto.ChargePointResult;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointUseRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.response.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PointController implements PointSpec {

    private final PointFacade pointFacade;

    @PostMapping("/api/v1/points/charge")
    public ApiResponse<PointResponse> chargePoint(@Valid @RequestBody PointChargeRequest request) {
        ChargePointResult result = pointFacade.charge(request.toChargeCommand());
        return ApiResponse.success(PointResponse.from(result));
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
