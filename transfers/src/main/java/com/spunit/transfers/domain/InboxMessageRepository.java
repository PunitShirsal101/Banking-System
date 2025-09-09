package com.spunit.transfers.domain;

import com.spunit.common.messaging.InboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxMessageRepository extends JpaRepository<InboxMessage, InboxMessage.Pk> {
}
