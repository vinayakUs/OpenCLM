INSERT INTO notification_template (
    event_type,
    channel,
    title_template,
    body_template
)
VALUES (
    'OTP_SENT',
    'EMAIL',
    'Your Login OTP',
    'Your one-time password is {{otp}}. It is valid for {{expiryMinutes}} minutes.'
)
ON CONFLICT (event_type, channel) DO NOTHING;

INSERT INTO notification_template (
    event_type,
    channel,
    title_template,
    body_template
)
VALUES (
    'WORKFLOW_CREATED',
    'IN_APP',
    'Workflow Created',
    'Workflow {{workflowName}} has been created.'
)
ON CONFLICT (event_type, channel) DO NOTHING;