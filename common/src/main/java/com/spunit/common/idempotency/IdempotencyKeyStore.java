package com.spunit.common.idempotency;

import java.time.Duration;
import java.util.Optional;

/**
 * Idempotency key store with TTL and payload hash.
 * Implementations must ensure atomic set-if-absent semantics.
 */
public interface IdempotencyKeyStore {
    /**
     * Attempts to store the idempotency key with the associated payload hash for the given TTL.
     * Returns true if stored (i.e., key was absent), false if already present.
     */
    boolean putIfAbsent(String key, String payloadHash, Duration ttl);

    /**
     * Retrieves the stored payload hash if the key exists and has not expired.
     */
    Optional<String> get(String key);
}
