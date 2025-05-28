package kr.hhplus.be.server.domain.outbox;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.support.event.EventStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.support.event.EventStatus.SEND_FAIL;
import static kr.hhplus.be.server.support.event.EventStatus.SEND_SUCCESS;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "outbox")
@Entity
public class Outbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long aggregateId;

    private String eventType;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @Column(columnDefinition = "json")
    private String payload;

    private LocalDateTime occurredAt;

    private Outbox(Long aggregateId, String eventType, EventStatus eventStatus, String payload, LocalDateTime occurredAt) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.eventStatus = eventStatus;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public static Outbox of(Long aggregateId, String eventType, EventStatus eventStatus, String payload) {
        return new Outbox(
                aggregateId,
                eventType,
                eventStatus,
                payload,
                LocalDateTime.now()
        );
    }

    public void markAsSuccess() {
        this.eventStatus = SEND_SUCCESS;
    }

    public void markAsFail() {
        this.eventStatus = SEND_FAIL;
    }
}
