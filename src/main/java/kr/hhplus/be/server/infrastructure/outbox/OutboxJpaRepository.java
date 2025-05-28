package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.support.event.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findByEventStatus(EventStatus eventStatus);

    Optional<Outbox> findByAggregateId(Long aggregateId);
}
