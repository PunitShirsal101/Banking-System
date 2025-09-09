package com.spunit.accounts.domain;

import java.util.Currency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Embedded
    private Balance balance;

    // Simple holds tracking to respect debit boundaries
    @Column(name = "hold_amount", nullable = false)
    private BigDecimal holdAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    protected Account() {}

    public Account(UUID id, Currency currency, BigDecimal initialAmount) {
        this.id = Objects.requireNonNull(id, "id");
        this.balance = new Balance(currency, initialAmount);
        this.status = AccountStatus.ACTIVE;
    }

    public UUID getId() { return id; }
    public long getVersion() { return version; }
    public AccountStatus getStatus() { return status; }
    public Balance getBalance() { return balance; }
    public BigDecimal getHoldAmount() { return holdAmount; }
    public BigDecimal getAvailable() { return balance.getAmount().subtract(holdAmount); }

    public void suspend() { this.status = AccountStatus.SUSPENDED; }
    public void close() { this.status = AccountStatus.CLOSED; }

    public void credit(BigDecimal amount) {
        ensureActive();
        balance.credit(amount);
    }

    public void debit(BigDecimal amount) {
        ensureActive();
        BigDecimal v = normalizePositive(amount);
        if (getAvailable().compareTo(v) < 0) {
            throw new IllegalStateException("Insufficient available funds");
        }
        balance.debit(v);
    }

    public void placeHold(BigDecimal amount) {
        ensureActive();
        BigDecimal v = normalizePositive(amount);
        if (getAvailable().compareTo(v) < 0) {
            throw new IllegalStateException("Insufficient available funds for hold");
        }
        holdAmount = holdAmount.add(v);
    }

    public void releaseHold(BigDecimal amount) {
        BigDecimal v = normalizePositive(amount);
        BigDecimal result = holdAmount.subtract(v);
        if (result.signum() < 0) {
            throw new IllegalArgumentException("Releasing more than held");
        }
        holdAmount = result;
    }

    private void ensureActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account not active");
        }
    }

    private static BigDecimal normalizePositive(BigDecimal v) {
        Objects.requireNonNull(v, "amount");
        if (v.signum() <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
