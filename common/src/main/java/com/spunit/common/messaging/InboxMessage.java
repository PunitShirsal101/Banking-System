package com.spunit.common.messaging;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity aligned with Flyway inbox table (composite PK).
 */
@Entity
@IdClass(InboxMessage.Pk.class)
@Table(name = "inbox")
public class InboxMessage {

    @Id
    @Column(name = "consumer_name", length = 120, nullable = false)
    private String consumerName;

    @Id
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "topic", length = 200, nullable = false)
    private String topic;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt = Instant.now();

    @Column(name = "processed_at")
    private Instant processedAt;

    protected InboxMessage() {}

    public InboxMessage(String consumerName, UUID messageId, String topic, String payload) {
        this.consumerName = consumerName;
        this.messageId = messageId;
        this.topic = topic;
        this.payload = payload;
        this.receivedAt = Instant.now();
    }

    public String getConsumerName() { return consumerName; }
    public UUID getMessageId() { return messageId; }
    public String getTopic() { return topic; }
    public String getPayload() { return payload; }
    public Instant getReceivedAt() { return receivedAt; }
    public Instant getProcessedAt() { return processedAt; }
    public void markProcessed(Instant when) { this.processedAt = when; }

    public static class Pk implements java.io.Serializable {
        private String consumerName;
        private UUID messageId;

        public Pk() {}

        public Pk(String consumerName, UUID messageId) {
            this.consumerName = consumerName;
            this.messageId = messageId;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(consumerName, messageId);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Pk other = (Pk) obj;
            return java.util.Objects.equals(consumerName, other.consumerName)
                    && java.util.Objects.equals(messageId, other.messageId);
        }
    }
}
