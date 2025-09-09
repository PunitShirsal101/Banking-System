package com.spunit.ledger.domain;

import java.util.Currency;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class LedgerEntry {
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    private EntryType type; // DEBIT or CREDIT

    private Currency currency;

    private BigDecimal amount; // positive value

    protected LedgerEntry() {}

    public LedgerEntry(UUID accountId, EntryType type, Currency currency, BigDecimal amount) {
        this.accountId = Objects.requireNonNull(accountId, "accountId");
        this.type = Objects.requireNonNull(type, "type");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.amount = normalizePositive(amount);
    }

    public UUID getAccountId() { return accountId; }
    public EntryType getType() { return type; }
    public Currency getCurrency() { return currency; }
    public BigDecimal getAmount() { return amount; }

    public BigDecimal signedAmount() {
        return type == EntryType.DEBIT ? amount : amount.negate();
    }

    private static BigDecimal normalizePositive(BigDecimal v) {
        Objects.requireNonNull(v, "amount");
        if (v.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
