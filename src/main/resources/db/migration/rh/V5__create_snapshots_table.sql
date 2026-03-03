-- src/main/resources/db/migration/rh/V5__create_snapshots_table.sql

CREATE TABLE rh.aggregate_snapshots (
    snapshot_id UUID PRIMARY KEY DEFAULT uuidv7(),  -- ← PostgreSQL 18 nativo!
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL,
    state JSONB NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE(aggregate_id, version)
);

CREATE INDEX idx_snapshots_aggregate_version
    ON rh.aggregate_snapshots(aggregate_id, version DESC);