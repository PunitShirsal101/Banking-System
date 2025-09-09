package com.spunit.payments.domain;

import java.util.Currency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private UUID id;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.CREATED;

    private UUID payerAccountId;
    private UUID payeeId;

    private Currency currency;

    private BigDecimal amount;

    private Instant createdAt = Instant.now();

    protected Payment() {}

    public Payment(UUID id, UUID payerAccountId, UUID payeeId, Currency currency, BigDecimal amount) {
        this.id = Objects.requireNonNull(id, "id");
        this.payerAccountId = Objects.requireNonNull(payerAccountId, "payerAccountId");
        this.payeeId = Objects.requireNonNull(payeeId, "payeeId");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.amount = normalizePositive(amount);
        this.status = PaymentStatus.CREATED;
    }

    public UUID getId() { return id; }
    public long getVersion() { return version; }
    public PaymentStatus getStatus() { return status; }
    public UUID getPayerAccountId() { return payerAccountId; }
    public UUID getPayeeId() { return payeeId; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }

    public void authorize() {
        if (status != PaymentStatus.CREATED) {
            throw new IllegalStateException("Only CREATED can be AUTHORIZED");
        }
        status = PaymentStatus.AUTHORIZED;
    }

    public void settle() {
        if (status != PaymentStatus.AUTHORIZED) {
            throw new IllegalStateException("Only AUTHORIZED can be SETTLED");
        }
        status = PaymentStatus.SETTLED;
    }

    public void fail() {
        if (status == PaymentStatus.SETTLED) {
            throw new IllegalStateException("Cannot fail SETTLED payment");
        }
        status = PaymentStatus.FAILED;
    }

    private static BigDecimal normalizePositive(BigDecimal v) {
        Objects.requireNonNull(v, "amount");
        if (v.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
