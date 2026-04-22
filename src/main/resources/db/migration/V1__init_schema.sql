-- =====================================================
-- Notification Service Schema
-- Database: PostgreSQL
-- =====================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Notifications
CREATE TABLE IF NOT EXISTS notifications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID           NOT NULL,
    loan_id         UUID,
    event_type      VARCHAR(30)    NOT NULL,
    channel         VARCHAR(10)    NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    subject         VARCHAR(255),
    body            TEXT,
    sent_at         TIMESTAMP,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_event_type CHECK (event_type IN ('LOAN_CREATED', 'DUE_DATE_REMINDER', 'REPAYMENT_RECEIVED', 'OVERDUE_NOTICE', 'LOAN_CLOSED', 'LOAN_CANCELLED', 'LATE_FEE_APPLIED', 'LOAN_WRITTEN_OFF', 'LIMIT_UPDATED')),
    CONSTRAINT chk_channel CHECK (channel IN ('EMAIL', 'SMS', 'PUSH')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'SENT', 'FAILED', 'RETRYING'))
);

-- Notification Templates
CREATE TABLE IF NOT EXISTS notification_templates (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type        VARCHAR(30)    NOT NULL,
    channel           VARCHAR(10)    NOT NULL,
    subject_template  VARCHAR(255)   NOT NULL,
    body_template     TEXT           NOT NULL,
    is_active         BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_template_event_channel UNIQUE (event_type, channel)
);

-- Notification Rules
CREATE TABLE IF NOT EXISTS notification_rules (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id        UUID,
    customer_segment  VARCHAR(50),
    event_type        VARCHAR(30)    NOT NULL,
    channel           VARCHAR(10)    NOT NULL,
    is_active         BOOLEAN        NOT NULL DEFAULT TRUE
);

-- Customer Notification Preferences
CREATE TABLE IF NOT EXISTS customer_notification_preferences (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id     UUID           NOT NULL,
    channel         VARCHAR(10)    NOT NULL,
    is_enabled      BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_customer_channel UNIQUE (customer_id, channel)
);

CREATE INDEX idx_notifications_customer ON notifications(customer_id);
CREATE INDEX idx_notifications_loan ON notifications(loan_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_templates_event_channel ON notification_templates(event_type, channel);
CREATE INDEX idx_rules_event ON notification_rules(event_type);
CREATE INDEX idx_preferences_customer ON customer_notification_preferences(customer_id);

