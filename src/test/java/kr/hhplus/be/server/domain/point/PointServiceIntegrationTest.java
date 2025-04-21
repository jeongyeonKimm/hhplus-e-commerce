package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class PointServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("사용자 아이디와 충전 금액을 받아 사용자의 포인트를 충전한다.")
    @Test
    void chargePoint() {
        long initialBalance = 1000L;
        User user = userRepository.save(User.of());
        pointRepository.savePoint(Point.of(user.getId(), initialBalance));

        long amount = 1000L;
        Point chargedPoint = pointService.chargePoint(user.getId(), amount);

        assertThat(chargedPoint.getBalance()).isEqualTo(initialBalance + amount);
    }

    @DisplayName("사용자 아이디와 사용 금액을 받아 사용자의 포인트를 사용한다.")
    @Test
    void usePoint() {
        long initialBalance = 3000L;
        User user = userRepository.save(User.of());
        pointRepository.savePoint(Point.of(user.getId(), initialBalance));

        long amount = 1000L;
        Point usedPoint = pointService.usePoint(user.getId(), amount);

        assertThat(usedPoint.getBalance()).isEqualTo(initialBalance - amount);
    }

    @DisplayName("사용자 아이디와 사용된 전체 금액을 받아 사용자의 포인트를 롤백한다.")
    @Test
    void rollbackPoint() {
        long initialBalance = 3000L;
        User user = userRepository.save(User.of());
        Point point = pointRepository.savePoint(Point.of(user.getId(), initialBalance));

        long totalAmount = 2000L;
        pointService.rollbackPoint(user.getId(), totalAmount);

        Point foundPoint = pointRepository.findPointByUserId(user.getId()).get();
        assertThat(foundPoint.getBalance()).isEqualTo(initialBalance + totalAmount);
    }
}
