package kr.hhplus.be.server.infrastructure.outbox;

import kr.hhplus.be.server.domain.outbox.Outbox;
import kr.hhplus.be.server.domain.outbox.OutboxRepository;
import kr.hhplus.be.server.support.event.EventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public List<Outbox> findByEventStatus(EventStatus eventStatus) {
        return outboxJpaRepository.findByEventStatus(eventStatus);
    }

    @Override
    public Optional<Outbox> findByAggregateId(Long aggregateId) {
        return outboxJpaRepository.findByAggregateId(aggregateId);
    }

    @Override
    public Optional<Outbox> findByPayload(String payload) {
        return outboxJpaRepository.findByPayload(payload);
    }
}
