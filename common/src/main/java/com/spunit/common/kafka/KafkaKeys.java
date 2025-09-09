package com.spunit.common.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * Helpers for consistent Kafka partition keys by business identifiers to
 * preserve ordering per aggregate across producers/consumers.
 */
public final class KafkaKeys {
    private KafkaKeys() {}

    public static byte[] accountKey(UUID accountId) {
        return uuidKey(accountId);
    }

    public static byte[] paymentKey(UUID paymentId) {
        return uuidKey(paymentId);
    }

    public static byte[] transferKey(UUID transferId) {
        return uuidKey(transferId);
    }

    public static byte[] stringKey(String key) {
        Objects.requireNonNull(key, "key");
        return key.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] uuidKey(UUID id) {
        Objects.requireNonNull(id, "id");
        // Use canonical string to ensure stable partitioning across languages
        return id.toString().getBytes(StandardCharsets.UTF_8);
    }
}
