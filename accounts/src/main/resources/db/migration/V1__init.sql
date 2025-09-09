-- Accounts schema (PostgreSQL)
-- Multi-tenancy: set search_path to ${TENANT_SCHEMA:public} at runtime if applicable.

CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    hold_amount NUMERIC(19,2) NOT NULL DEFAULT 0
);
