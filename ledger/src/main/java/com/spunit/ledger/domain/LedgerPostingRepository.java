package com.spunit.ledger.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerPostingRepository extends JpaRepository<LedgerPosting, UUID> {
}
