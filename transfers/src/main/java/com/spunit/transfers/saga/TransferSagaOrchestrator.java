package com.spunit.transfers.saga;

import com.spunit.common.messaging.OutboxMessage;
import com.spunit.transfers.domain.OutboxMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Minimal Saga orchestrator for Transfers using transactional outbox.
 * Writes idempotent outbox events for start/compensate. Full orchestration of
 * holds, credits/debits, postings, and notifications can be driven by consumers of these events.
 */
@Service
public class TransferSagaOrchestrator {

    private static final String AGGREGATE_TYPE = "Transfer";
    private static final String EVT_SAGA_STARTED = "TransferSagaStarted";
    private static final String EVT_SAGA_COMPENSATE = "TransferSagaCompensate";

    private final OutboxMessageRepository outboxRepo;

    public TransferSagaOrchestrator(OutboxMessageRepository outboxRepo) {
        this.outboxRepo = outboxRepo;
    }

    /**
     * Starts a transfer saga for the given transferId.
     * Idempotent: if the start event already exists for this transfer, it is a no-op.
     */
    @Transactional
    public void start(UUID transferId) {
        boolean exists = outboxRepo.existsByAggregateTypeAndAggregateIdAndEventType(AGGREGATE_TYPE, transferId, EVT_SAGA_STARTED);
        if (exists) {
            return;
        }
        String payload = "{\"transferId\":\"" + transferId + "\",\"event\":\"" + EVT_SAGA_STARTED + "\"}";
        OutboxMessage msg = new OutboxMessage(UUID.randomUUID(), AGGREGATE_TYPE, transferId, EVT_SAGA_STARTED, payload, null);
        outboxRepo.save(msg);
    }

    /**
     * Compensate a failed or timed-out transfer saga by emitting a compensation event.
     * Idempotent: if a compensation event already exists for this transfer, it is a no-op.
     */
    @Transactional
    public void compensate(UUID transferId, String reason) {
        boolean exists = outboxRepo.existsByAggregateTypeAndAggregateIdAndEventType(AGGREGATE_TYPE, transferId, EVT_SAGA_COMPENSATE);
        if (exists) {
            return;
        }
        String safeReason = reason == null ? "" : reason.replace("\"", "\\\"");
        String payload = "{\"transferId\":\"" + transferId + "\",\"event\":\"" + EVT_SAGA_COMPENSATE + "\",\"reason\":\"" + safeReason + "\"}";
        OutboxMessage msg = new OutboxMessage(UUID.randomUUID(), AGGREGATE_TYPE, transferId, EVT_SAGA_COMPENSATE, payload, null);
        outboxRepo.save(msg);
    }
}
