-- Payments schema (PostgreSQL)
-- Multi-tenancy: set search_path to ${TENANT_SCHEMA:public} at runtime if applicable.

CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    payerAccountId UUID NOT NULL,
    payeeId UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    createdAt TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_payments_payer ON payments(payerAccountId);
CREATE INDEX IF NOT EXISTS idx_payments_created_at ON payments(createdAt);
