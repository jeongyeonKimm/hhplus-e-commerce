package kr.hhplus.be.server.api.point.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PointChargeRequest {

    private Long userId;
    private Integer chargeAmount;

}
