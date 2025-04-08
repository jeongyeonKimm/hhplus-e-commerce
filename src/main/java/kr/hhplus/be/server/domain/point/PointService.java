package kr.hhplus.be.server.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private Long generateId() {
        return sequence++;
    }
}
