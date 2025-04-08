package kr.hhplus.be.server.domain.point;

import java.util.Optional;

public interface PointRepository {

    Point save(Point point);

    Optional<Point> findByUserId(Long userId);
}
