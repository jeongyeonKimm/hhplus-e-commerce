package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderCustomRepository {

    List<Order> findPaidOrdersBetween(LocalDateTime start, LocalDateTime end);
}
