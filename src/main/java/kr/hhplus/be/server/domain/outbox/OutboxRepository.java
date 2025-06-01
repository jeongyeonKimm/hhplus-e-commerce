package kr.hhplus.be.server.domain.outbox;

import kr.hhplus.be.server.support.event.EventStatus;

import java.util.List;
import java.util.Optional;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    List<Outbox> findByEventStatus(EventStatus eventStatus);

    Optional<Outbox> findByAggregateId(Long aggregateId);
}
