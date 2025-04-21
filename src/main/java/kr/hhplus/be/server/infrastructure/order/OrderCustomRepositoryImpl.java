package kr.hhplus.be.server.infrastructure.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.order.QOrder;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Order> findPaidOrdersBetween(LocalDateTime start, LocalDateTime end) {
        QOrder order = QOrder.order;

        return jpaQueryFactory.selectFrom(order)
                .where(order.status.eq(OrderStatus.PAID)
                        .and(order.createdAt.between(start, end)))
                .fetch();
    }
}
