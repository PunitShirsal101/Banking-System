package com.spunit.transfers.domain;

import java.util.Currency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transfers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_transfers_idempotency", columnNames = {"idempotency_key"})
})
public class Transfer {
    @Id
    private UUID id;

    @Version
    private long version;

    private UUID fromAccountId;
    private UUID toAccountId;

    private Currency currency;

    private BigDecimal amount;

    @Column(name = "idempotency_key", nullable = false, updatable = false, length = 120)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    private TransferStatus status = TransferStatus.PENDING;

    private Instant createdAt = Instant.now();

    protected Transfer() {}

    public Transfer(UUID id, UUID fromAccountId, UUID toAccountId, Currency currency, BigDecimal amount, String idempotencyKey) {
        this.id = Objects.requireNonNull(id, "id");
        this.fromAccountId = Objects.requireNonNull(fromAccountId, "fromAccountId");
        this.toAccountId = Objects.requireNonNull(toAccountId, "toAccountId");
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("from and to accounts must differ");
        }
        this.currency = Objects.requireNonNull(currency, "currency");
        this.amount = normalizePositive(amount);
        this.idempotencyKey = Objects.requireNonNull(idempotencyKey, "idempotencyKey");
        this.status = TransferStatus.PENDING;
    }

    public UUID getId() { return id; }
    public long getVersion() { return version; }
    public UUID getFromAccountId() { return fromAccountId; }
    public UUID getToAccountId() { return toAccountId; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getAmount() { return amount; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public TransferStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void complete() {
        if (status != TransferStatus.PENDING) {
            throw new IllegalStateException("Only PENDING can complete");
        }
        status = TransferStatus.COMPLETED;
    }

    public void fail() {
        if (status == TransferStatus.COMPLETED) {
            throw new IllegalStateException("Cannot fail COMPLETED transfer");
        }
        status = TransferStatus.FAILED;
    }

    private static BigDecimal normalizePositive(BigDecimal v) {
        Objects.requireNonNull(v, "amount");
        if (v.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
