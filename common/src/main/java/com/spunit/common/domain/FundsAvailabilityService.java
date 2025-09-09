package com.spunit.common.domain;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Determines if funds are available for debits or holds on an account.
 */
public interface FundsAvailabilityService {
    boolean isAvailable(UUID accountId, BigDecimal amount);
}
