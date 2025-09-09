package com.spunit.payments.domain;

import java.util.Currency;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class PaymentFactory {
    private PaymentFactory() {}

    /**
     * Creates a payment with pre-validated payee.
     */
    public static Payment create(UUID id, UUID payerAccountId, UUID validatedPayeeId, Currency currency, BigDecimal amount) {
        Objects.requireNonNull(validatedPayeeId, "validatedPayeeId");
        return new Payment(id, payerAccountId, validatedPayeeId, currency, amount);
    }
}
