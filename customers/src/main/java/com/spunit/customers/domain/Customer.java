package com.spunit.customers.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document("customers")
public class Customer {
    @Id
    private UUID id;

    private String firstName;
    private String lastName;
    private ContactInfo contactInfo;
    private KycStatus kycStatus = KycStatus.PENDING;
    private Instant createdAt = Instant.now();

    public Customer() {}

    public Customer(UUID id, String firstName, String lastName, ContactInfo contactInfo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactInfo = contactInfo;
        this.kycStatus = KycStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public ContactInfo getContactInfo() { return contactInfo; }
    public KycStatus getKycStatus() { return kycStatus; }
    public Instant getCreatedAt() { return createdAt; }

    public void markKycVerified() { this.kycStatus = KycStatus.VERIFIED; }
    public void markKycRejected() { this.kycStatus = KycStatus.REJECTED; }
}
