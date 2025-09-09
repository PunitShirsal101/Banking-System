package com.spunit.common.domain;

import java.util.Map;

/**
 * Calculates a risk score for a payment or transfer.
 */
public interface RiskScoringService {
    /**
     * Returns a score between 0.0 (low) and 1.0 (high risk).
     */
    double score(Map<String, Object> attributes);
}
