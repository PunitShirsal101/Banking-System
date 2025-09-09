package com.spunit.common.domain;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Provides FX conversion rates.
 */
public interface FXRateService {
    BigDecimal convert(BigDecimal amount, Currency from, Currency to);
}
