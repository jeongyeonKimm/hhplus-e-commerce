package kr.hhplus.be.server.interfaces.api.point;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.dto.command.GetPointCommand;
import kr.hhplus.be.server.application.point.dto.result.PointResult;
import kr.hhplus.be.server.common.response.ApiResponse;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointChargeRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.request.PointUseRequest;
import kr.hhplus.be.server.interfaces.api.point.dto.response.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
@RestController
public class PointController implements PointSpec {

    private final PointFacade pointFacade;
    private final PaymentFacade paymentFacade;

    @PostMapping("/charge")
    public ApiResponse<PointResponse> chargePoint(@Valid @RequestBody PointChargeRequest request) {
        PointResult result = pointFacade.charge(request.toChargeCommand());
        return ApiResponse.success(PointResponse.from(result));
    }

    @GetMapping
    public ApiResponse<PointResponse> getPoint(@Positive @RequestParam Long userId) {
        PointResult result = pointFacade.getPoint(GetPointCommand.of(userId));
        return ApiResponse.success(PointResponse.from(result));
    }

    @PostMapping("/use")
    public ApiResponse<PointResponse> usePoint(@Valid @RequestBody PointUseRequest request) {
        paymentFacade.payment(request.toPaymentCommand());
        return ApiResponse.successWithNoContent();
    }
}
