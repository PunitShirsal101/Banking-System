package com.spunit.accounts.domain;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@Embeddable
public class Balance {
    private Currency currency;

    private BigDecimal amount;

    protected Balance() {}

    public Balance(Currency currency, BigDecimal amount) {
        this.currency = Objects.requireNonNull(currency, "currency");
        this.amount = normalizeNonNegative(amount);
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void credit(BigDecimal delta) {
        BigDecimal v = normalizePositive(delta);
        this.amount = this.amount.add(v);
    }

    public void debit(BigDecimal delta) {
        BigDecimal v = normalizePositive(delta);
        BigDecimal result = this.amount.subtract(v);
        if (result.signum() < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.amount = result;
    }

    private static BigDecimal normalizeNonNegative(BigDecimal v) {
        Objects.requireNonNull(v, "amount");
        if (v.signum() < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal normalizePositive(BigDecimal v) {
        Objects.requireNonNull(v, "amount");
        if (v.signum() <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
