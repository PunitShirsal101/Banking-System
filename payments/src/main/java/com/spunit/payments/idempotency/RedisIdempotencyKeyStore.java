package com.spunit.payments.idempotency;

import com.spunit.common.idempotency.IdempotencyKeyStore;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;

public class RedisIdempotencyKeyStore implements IdempotencyKeyStore {
    private final StringRedisTemplate redis;

    public RedisIdempotencyKeyStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    private static String k(String key) {
        return "idem:" + key;
    }

    @Override
    public boolean putIfAbsent(String key, String payloadHash, Duration ttl) {
        Boolean ok = redis.opsForValue().setIfAbsent(k(key), payloadHash, ttl);
        return Boolean.TRUE.equals(ok);
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(redis.opsForValue().get(k(key)));
    }
}
