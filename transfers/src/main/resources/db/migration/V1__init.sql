-- Transfers schema (PostgreSQL)
-- Multi-tenancy: set search_path to ${TENANT_SCHEMA:public} at runtime if applicable.

CREATE TABLE IF NOT EXISTS transfers (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL,
    fromAccountId UUID NOT NULL,
    toAccountId UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    idempotency_key VARCHAR(120) NOT NULL,
    status VARCHAR(32) NOT NULL,
    createdAt TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_transfers_idempotency UNIQUE (idempotency_key)
);

CREATE INDEX IF NOT EXISTS idx_transfers_from ON transfers(fromAccountId);
CREATE INDEX IF NOT EXISTS idx_transfers_created_at ON transfers(createdAt);
