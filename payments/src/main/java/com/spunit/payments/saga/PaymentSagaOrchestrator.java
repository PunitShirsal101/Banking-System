package com.spunit.payments.saga;

import com.spunit.common.messaging.OutboxMessage;
import com.spunit.payments.domain.OutboxMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Minimal Saga orchestrator for Payments using transactional outbox.
 * Writes idempotent outbox events for start/compensate. Full orchestration of
 * holds, risk, postings, and notifications can be driven by consumers of these events.
 */
@Service
public class PaymentSagaOrchestrator {

    private static final String AGGREGATE_TYPE = "Payment";
    private static final String EVT_SAGA_STARTED = "PaymentSagaStarted";
    private static final String EVT_SAGA_COMPENSATE = "PaymentSagaCompensate";

    private final OutboxMessageRepository outboxRepo;

    public PaymentSagaOrchestrator(OutboxMessageRepository outboxRepo) {
        this.outboxRepo = outboxRepo;
    }

    /**
     * Starts a payment saga with given paymentId.
     * Idempotent: if the start event already exists for this payment, it is a no-op.
     */
    @Transactional
    public void start(UUID paymentId) {
        boolean exists = outboxRepo.existsByAggregateTypeAndAggregateIdAndEventType(AGGREGATE_TYPE, paymentId, EVT_SAGA_STARTED);
        if (exists) {
            return;
        }
        String payload = "{\"paymentId\":\"" + paymentId + "\",\"event\":\"" + EVT_SAGA_STARTED + "\"}";
        OutboxMessage msg = new OutboxMessage(UUID.randomUUID(), AGGREGATE_TYPE, paymentId, EVT_SAGA_STARTED, payload, null);
        outboxRepo.save(msg);
    }

    /**
     * Compensates a failed or timed out saga by emitting a compensation event.
     * Idempotent: if a compensation event already exists for this payment, it is a no-op.
     */
    @Transactional
    public void compensate(UUID paymentId, String reason) {
        boolean exists = outboxRepo.existsByAggregateTypeAndAggregateIdAndEventType(AGGREGATE_TYPE, paymentId, EVT_SAGA_COMPENSATE);
        if (exists) {
            return;
        }
        String safeReason = reason == null ? "" : reason.replace("\"", "\\\"");
        String payload = "{\"paymentId\":\"" + paymentId + "\",\"event\":\"" + EVT_SAGA_COMPENSATE + "\",\"reason\":\"" + safeReason + "\"}";
        OutboxMessage msg = new OutboxMessage(UUID.randomUUID(), AGGREGATE_TYPE, paymentId, EVT_SAGA_COMPENSATE, payload, null);
        outboxRepo.save(msg);
    }
}
