package com.spunit.accounts.domain;

import java.util.Currency;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class AccountFactory {
    private AccountFactory() {}

    /**
     * Creates an account with an initial deposit, optionally placing an initial hold.
     */
    public static Account createWithInitialDepositAndHold(UUID id, Currency currency, BigDecimal initialDeposit, BigDecimal initialHold) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(initialDeposit, "initialDeposit");
        if (initialDeposit.signum() < 0) {
            throw new IllegalArgumentException("initialDeposit must be >= 0");
        }
        if (initialHold == null) initialHold = BigDecimal.ZERO;
        if (initialHold.signum() < 0) {
            throw new IllegalArgumentException("initialHold must be >= 0");
        }
        Account acc = new Account(id, currency, initialDeposit);
        if (initialHold.signum() > 0) {
            acc.placeHold(initialHold);
        }
        return acc;
    }
}
