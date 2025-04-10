package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.hhplus.be.server.common.exception.ErrorCode.POINT_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointRepository pointRepository;
    private long sequence = 1L;

    public Point chargePoint(Long userId, Integer amount) {
        Point point = pointRepository.findByUserId(userId)
                .orElseGet(() -> Point.create(generateId(), userId, 0));

        point.charge(amount);
        return pointRepository.save(point);
    }

    public Point getPoint(Long userId) {
        return pointRepository.findByUserId(userId)
                .orElseGet(() -> Point.create(generateId(), userId, 0));
    }

    public Point usePoint(Long userId, Integer amount) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(POINT_NOT_EXIST));

        point.use(amount);
        return pointRepository.save(point);
    }

    private Long generateId() {
        return sequence++;
    }
}
