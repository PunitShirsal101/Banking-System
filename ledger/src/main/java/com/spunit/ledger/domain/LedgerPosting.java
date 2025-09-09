package com.spunit.ledger.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "ledger_postings")
public class LedgerPosting {
    @Id
    private UUID id;

    @Version
    private long version;

    private Instant createdAt = Instant.now();

    @ElementCollection
    @CollectionTable(name = "ledger_posting_entries", joinColumns = @JoinColumn(name = "posting_id"))
    private List<LedgerEntry> entries = new ArrayList<>();

    protected LedgerPosting() {}

    public LedgerPosting(UUID id, List<LedgerEntry> entries) {
        this.id = Objects.requireNonNull(id, "id");
        this.entries = new ArrayList<>(Objects.requireNonNull(entries, "entries"));
        validateBalanced();
    }

    public UUID getId() { return id; }
    public long getVersion() { return version; }
    public Instant getCreatedAt() { return createdAt; }
    public List<LedgerEntry> getEntries() { return Collections.unmodifiableList(entries); }

    @PrePersist
    @PreUpdate
    private void validateBalanced() {
        if (entries.isEmpty()) {
            throw new IllegalStateException("Posting must contain at least one entry");
        }
        // Ensure single currency
        var currency = entries.get(0).getCurrency();
        boolean singleCurrency = entries.stream().allMatch(e -> e.getCurrency() == currency);
        if (!singleCurrency) {
            throw new IllegalStateException("All entries must have the same currency");
        }
        BigDecimal sum = entries.stream()
                .map(LedgerEntry::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.signum() != 0) {
            throw new IllegalStateException("Entries must balance to zero");
        }
    }
}
