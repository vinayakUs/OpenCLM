CREATE TABLE IF NOT EXISTS contract (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,         -- 👈 CONTRACT NAME (human readable)
    workflow_id UUID NOT NULL,          -- source workflow
    status VARCHAR(30) NOT NULL,        -- DRAFT / IN_REVIEW / APPROVED / SIGNED / REJECTED
    created_by UUID NOT NULL,
    updated_by UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_contract_name ON contract(name);
CREATE INDEX IF NOT EXISTS idx_contract_workflow_id ON contract(workflow_id);
CREATE INDEX IF NOT EXISTS idx_contract_status ON contract(status);

-- EVENT EMIT OUTBOX --

CREATE TABLE IF NOT EXISTS outbox_event
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Aggregate information
    aggregate_type VARCHAR(20) NOT NULL , --which domain emitted the event ie CONTRACT
    aggregate_id UUID NOT NULL , --domain id for emitted event ie contract_id
    -- Event Identity
    event_type VARCHAR(50) not null ,
    event_id uuid not null unique ,
    -- Delivery tracking
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',   -- NEW / SENT / FAILED
    retry_count INT NOT NULL DEFAULT 0,
        -- Event Payload
    payload JSONB NOT NULL,
    -- Timestamp
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_attempt_at TIMESTAMPTZ
);

CREATE INDEX if not exists idx_outbox_status_created
    ON outbox_event (status, created_at);
CREATE INDEX if not exists idx_outbox_aggregate
    ON outbox_event (aggregate_type, aggregate_id);

-- PROCESSED EVENT TABLE FOR EVENT TRACKING --

CREATE TABLE IF NOT EXISTS processed_event (
    event_id UUID PRIMARY KEY,
    processed_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_processed_event_id ON processed_event(event_id);

