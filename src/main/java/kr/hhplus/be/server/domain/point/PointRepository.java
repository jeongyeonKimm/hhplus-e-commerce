package kr.hhplus.be.server.domain.point;

import java.util.Optional;

public interface PointRepository {

    Point savePoint(Point point);

    Optional<Point> findPointByUserId(Long userId);

    void savePointHistory(PointHistory pointHistory);
}
