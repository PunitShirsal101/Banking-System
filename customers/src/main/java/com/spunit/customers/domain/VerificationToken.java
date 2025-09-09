package com.spunit.customers.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document("verification_tokens")
public class VerificationToken {
    @Id
    private UUID id;
    private UUID customerId;
    private String token;

    @Indexed(expireAfterSeconds = 3600) // TTL: 1 hour after createdAt
    private Instant createdAt = Instant.now();

    public VerificationToken() {}

    public VerificationToken(UUID id, UUID customerId, String token) {
        this.id = id;
        this.customerId = customerId;
        this.token = token;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getToken() { return token; }
    public Instant getCreatedAt() { return createdAt; }
}
