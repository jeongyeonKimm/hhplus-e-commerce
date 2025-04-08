package kr.hhplus.be.server.domain.point;

import java.util.Optional;

public interface PointRepository {

    Point save();

    Optional<Point> findByUserId(Long userId);
}
