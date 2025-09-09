package com.spunit.common.kafka;

/**
 * Central registry of Kafka topic names used across services. This helps avoid typos
 * and keeps producers/consumers aligned when wiring configurations.
 */
public final class Topics {
    private Topics() {}

    public static final String PAYMENTS_EVENTS = "payments.events";
    public static final String LEDGER_POSTED = "ledger.posted";
    public static final String ACCOUNTS_BALANCE_UPDATED = "accounts.balance-updated"; // compacted
    public static final String NOTIFICATIONS_OUT = "notifications.out";
}
