-- Ledger schema (PostgreSQL)
-- Multi-tenancy: set search_path to ${TENANT_SCHEMA:public} at runtime if applicable.

CREATE TABLE IF NOT EXISTS ledger_postings (
    id UUID PRIMARY KEY,
    version BIGINT NOT NULL,
    createdAt TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS ledger_posting_entries (
    entry_id BIGSERIAL PRIMARY KEY,
    posting_id UUID NOT NULL,
    accountId UUID NOT NULL,
    type VARCHAR(10) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    CONSTRAINT fk_posting FOREIGN KEY (posting_id) REFERENCES ledger_postings(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ledger_entries_posting ON ledger_posting_entries(posting_id);
