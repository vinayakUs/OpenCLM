CREATE TABLE IF NOT EXISTS workflow_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    -- Reference to file_storage table
    template_file_id UUID NOT NULL,
    -- DRAFT (still designing), LIVE (active workflow)
    current_status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    version INT NOT NULL DEFAULT 1,
    -- auditing columns (populated by JPA Auditing)
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
    );

-- indexes
CREATE INDEX IF NOT EXISTS idx_workflow_template_status
    ON workflow_template(current_status);

CREATE INDEX IF NOT EXISTS idx_workflow_template_created_by
    ON workflow_template(created_by);

CREATE INDEX IF NOT EXISTS idx_workflow_template_created_at
    ON workflow_template(created_at);

CREATE INDEX IF NOT EXISTS idx_workflow_template_updated_by
    ON workflow_template(updated_by);


CREATE TABLE IF NOT EXISTS workflow_variable (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workflow_id UUID NOT NULL
    REFERENCES workflow_template(id) ON DELETE CASCADE,
    variable_name VARCHAR(255) NOT NULL,  -- e.g. party_name
    data_type VARCHAR(50) NOT NULL,       -- STRING, DATE, CHECKBOX
    label VARCHAR(255),                   -- Optional UI label
    required BOOLEAN NOT NULL DEFAULT FALSE,
    default_value TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    -- auditing columns
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_by UUID NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
    );

-- indexes
CREATE INDEX IF NOT EXISTS idx_workflow_variable_workflow_id
    ON workflow_variable(workflow_id);
CREATE INDEX IF NOT EXISTS idx_workflow_variable_sort_order
    ON workflow_variable(sort_order);
-- optional composite index (very common query)
CREATE INDEX IF NOT EXISTS idx_workflow_variable_workflow_sort
    ON workflow_variable(workflow_id, sort_order);


CREATE TABLE IF NOT EXISTS workflow_form_field (
                                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    workflow_id UUID NOT NULL
    REFERENCES workflow_template(id) ON DELETE CASCADE,

    variable_id UUID NOT NULL
    REFERENCES workflow_variable(id) ON DELETE CASCADE,

    label VARCHAR(255),
    placeholder VARCHAR(255),

    sort_order INT NOT NULL DEFAULT 0,

    visibility_rule TEXT,   -- future conditional logic
    validation_rule TEXT,   -- regex or min-max constraints

-- auditing columns
    created_by UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,

    updated_by UUID NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
    );

-- indexes
CREATE INDEX IF NOT EXISTS idx_form_field_workflow_id
    ON workflow_form_field(workflow_id);

CREATE INDEX IF NOT EXISTS idx_form_field_variable_id
    ON workflow_form_field(variable_id);

CREATE INDEX IF NOT EXISTS idx_form_field_sort_order
    ON workflow_form_field(sort_order);

-- common composite index
CREATE INDEX IF NOT EXISTS idx_form_field_workflow_sort
    ON workflow_form_field(workflow_id, sort_order);
