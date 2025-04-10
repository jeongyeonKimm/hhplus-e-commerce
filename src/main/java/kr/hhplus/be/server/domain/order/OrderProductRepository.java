package kr.hhplus.be.server.domain.order;

import java.util.List;

public interface OrderProductRepository {

    void save(OrderProduct orderProduct);

    List<OrderProduct> findByOrderId(Long orderId);
}
