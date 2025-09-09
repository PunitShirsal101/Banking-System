package com.spunit.payments.config;

import com.spunit.common.idempotency.IdempotencyKeyStore;
import com.spunit.payments.idempotency.RedisIdempotencyKeyStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class IdempotencyConfig {

    @Bean
    public IdempotencyKeyStore idempotencyKeyStore(StringRedisTemplate template) {
        return new RedisIdempotencyKeyStore(template);
    }
}
