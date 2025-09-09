package com.spunit.common.spi.impl;

import com.spunit.common.spi.FormatService;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Default SPI implementation.
 * Core Java – Advanced Concepts: ServiceLoader & SPI
 * - Public no-arg constructor required; keep implementation minimal.
 */
public class SimpleFormatService implements FormatService {
    public SimpleFormatService() {}

    @Override
    public String formatAmount(long cents, String currencyCode) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        try {
            if (currencyCode != null && !currencyCode.isBlank()) {
                nf.setCurrency(Currency.getInstance(currencyCode));
            }
        } catch (Exception ignored) { }
        double amount = cents / 100.0;
        return nf.format(amount);
    }
}
