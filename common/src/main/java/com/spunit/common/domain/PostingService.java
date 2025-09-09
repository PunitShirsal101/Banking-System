package com.spunit.common.domain;

import java.util.List;

/**
 * Posts ledger entries atomically (double-entry invariant).
 */
public interface PostingService {
    /**
     * Posts a set of entries atomically. Implementations must guarantee that the sum of debits equals credits.
     */
    void post(List<?> entries);
}
