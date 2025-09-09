package com.spunit.accounts.domain;

import com.spunit.common.messaging.InboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboxMessageRepository extends JpaRepository<InboxMessage, InboxMessage.Pk> {
}
