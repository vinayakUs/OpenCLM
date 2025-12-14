CREATE TABLE IF NOT EXISTS notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- internal user (for in-app)
    user_id UUID,

    -- external recipient (email / phone)
    recipient VARCHAR(255),

    title VARCHAR(255),
    message TEXT NOT NULL,

    -- what this notification is about
    reference_type VARCHAR(50),   -- WORKFLOW / CONTRACT / COMMENT / MENTION
    reference_id UUID,            -- workflowId / contractId / commentId

    -- how it should be delivered
    channel VARCHAR(20) NOT NULL, -- IN_APP / EMAIL / SMS / LOG

    -- delivery + UI state
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING / SENT / FAILED
    is_read BOOLEAN DEFAULT FALSE,         -- only for IN_APP
    show_in_bell BOOLEAN DEFAULT FALSE,    -- whether to show in notification bell

    created_at TIMESTAMPTZ DEFAULT NOW(),
    sent_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_notification_user_id ON notification(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_channel ON notification(channel);
CREATE INDEX IF NOT EXISTS idx_notification_is_read ON notification(is_read);
CREATE INDEX IF NOT EXISTS idx_notification_show_in_bell ON notification(show_in_bell);
CREATE INDEX IF NOT EXISTS idx_notification_reference
    ON notification(reference_type, reference_id);



CREATE TABLE IF NOT EXISTS notification_template (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    event_type VARCHAR(50) NOT NULL,  -- WORKFLOW_CREATED, CONTRACT_REVIEW_REQUESTED
    channel VARCHAR(20) NOT NULL,     -- IN_APP / EMAIL / SMS

    title_template VARCHAR(255),
    body_template TEXT NOT NULL,

    created_at TIMESTAMPTZ DEFAULT NOW(),

    UNIQUE (event_type, channel)
);
