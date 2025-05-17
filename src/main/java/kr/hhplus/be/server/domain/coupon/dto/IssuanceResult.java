package kr.hhplus.be.server.domain.coupon.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class IssuanceResult {

    private final List<Long> successUserIds = new ArrayList<>();
    private final List<Long> failUserIds = new ArrayList<>();

    public void addSuccess(Long userId) {
        successUserIds.add(userId);
    }

    public void addFail(Long userId) {
        failUserIds.add(userId);
    }
}
