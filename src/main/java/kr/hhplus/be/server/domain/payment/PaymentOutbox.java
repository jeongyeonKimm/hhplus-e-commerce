package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.support.event.EventStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static kr.hhplus.be.server.support.event.EventStatus.SEND_FAIL;
import static kr.hhplus.be.server.support.event.EventStatus.SEND_SUCCESS;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment_outbox")
@Entity
public class PaymentOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String eventId;

    private String eventType;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @Column(columnDefinition = "json")
    private String payload;

    private LocalDateTime occurredAt;

    private PaymentOutbox(Long orderId, String eventId, String eventType, EventStatus eventStatus, String payload, LocalDateTime occurredAt) {
        this.orderId = orderId;
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventStatus = eventStatus;
        this.payload = payload;
        this.occurredAt = occurredAt;
    }

    public static PaymentOutbox of(Long orderId, String eventId, String eventType, EventStatus eventStatus, String payload) {
        return new PaymentOutbox(
                orderId,
                eventId,
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
