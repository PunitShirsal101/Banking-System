package com.spunit.transfers.domain;

import com.spunit.common.messaging.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, UUID> {
    boolean existsByAggregateTypeAndAggregateIdAndEventType(String aggregateType, UUID aggregateId, String eventType);
}
