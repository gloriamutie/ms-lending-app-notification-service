-- =====================================================
-- Notification Service Seed Data
-- =====================================================

-- Templates: Loan Created
INSERT INTO notification_templates (id, event_type, channel, subject_template, body_template, is_active)
VALUES ('t1111111-1111-1111-1111-111111111111', 'LOAN_CREATED', 'EMAIL', 'Loan Disbursed - {{productName}}', 'Dear {{customerName}}, your loan of KES {{loanAmount}} has been disbursed under {{productName}}. Due date: {{dueDate}}. Thank you for choosing our services.', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_templates (id, event_type, channel, subject_template, body_template, is_active)
VALUES ('t1111111-1111-1111-1111-111111111112', 'LOAN_CREATED', 'SMS', 'Loan Disbursed', 'Dear {{customerName}}, KES {{loanAmount}} disbursed. Due: {{dueDate}}.', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Templates: Due Date Reminder
INSERT INTO notification_templates (id, event_type, channel, subject_template, body_template, is_active)
VALUES ('t2222222-2222-2222-2222-222222222221', 'DUE_DATE_REMINDER', 'EMAIL', 'Payment Reminder - Due {{dueDate}}', 'Dear {{customerName}}, this is a reminder that your loan payment of KES {{loanAmount}} is due on {{dueDate}}. Please make your payment on time to avoid late fees.', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_templates (id, event_type, channel, subject_template, body_template, is_active)
VALUES ('t2222222-2222-2222-2222-222222222222', 'DUE_DATE_REMINDER', 'SMS', 'Payment Reminder', 'Reminder: KES {{loanAmount}} due on {{dueDate}}. Pay on time to avoid penalties.', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Templates: Repayment Received
INSERT INTO notification_templates (id, event_type, channel, subject_template, body_template, is_active)
VALUES ('t3333333-3333-3333-3333-333333333331', 'REPAYMENT_RECEIVED', 'EMAIL', 'Payment Received - Thank You', 'Dear {{customerName}}, we have received your payment of KES {{loanAmount}}. Thank you for your prompt payment.', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Templates: Overdue Notice
INSERT INTO notification_templates (id, event_type, channel, subject_template, body_template, is_active)
VALUES ('t4444444-4444-4444-4444-444444444441', 'OVERDUE_NOTICE', 'EMAIL', 'URGENT: Loan Payment Overdue', 'Dear {{customerName}}, your loan payment of KES {{loanAmount}} was due on {{dueDate}} and is now overdue. Please make payment immediately to avoid additional fees.', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_templates (id, event_type, channel, subject_template, body_template, is_active)
VALUES ('t4444444-4444-4444-4444-444444444442', 'OVERDUE_NOTICE', 'SMS', 'Payment Overdue', 'URGENT: Your loan payment is overdue. Pay KES {{loanAmount}} immediately to avoid penalties.', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Templates: Late Fee Applied
INSERT INTO notification_templates (id, event_type, channel, subject_template, body_template, is_active)
VALUES ('t5555555-5555-5555-5555-555555555551', 'LATE_FEE_APPLIED', 'EMAIL', 'Late Fee Applied to Your Loan', 'Dear {{customerName}}, a late fee of KES {{loanAmount}} has been applied to your loan. Please settle your outstanding balance promptly.', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Default notification rules (all products)
INSERT INTO notification_rules (id, product_id, customer_segment, event_type, channel, is_active)
VALUES ('r1111111-1111-1111-1111-111111111111', NULL, NULL, 'LOAN_CREATED', 'EMAIL', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_rules (id, product_id, customer_segment, event_type, channel, is_active)
VALUES ('r1111111-1111-1111-1111-111111111112', NULL, NULL, 'LOAN_CREATED', 'SMS', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_rules (id, product_id, customer_segment, event_type, channel, is_active)
VALUES ('r2222222-2222-2222-2222-222222222221', NULL, NULL, 'OVERDUE_NOTICE', 'EMAIL', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_rules (id, product_id, customer_segment, event_type, channel, is_active)
VALUES ('r2222222-2222-2222-2222-222222222222', NULL, NULL, 'OVERDUE_NOTICE', 'SMS', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_rules (id, product_id, customer_segment, event_type, channel, is_active)
VALUES ('r3333333-3333-3333-3333-333333333331', NULL, NULL, 'REPAYMENT_RECEIVED', 'EMAIL', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_rules (id, product_id, customer_segment, event_type, channel, is_active)
VALUES ('r4444444-4444-4444-4444-444444444441', NULL, NULL, 'DUE_DATE_REMINDER', 'EMAIL', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO notification_rules (id, product_id, customer_segment, event_type, channel, is_active)
VALUES ('r4444444-4444-4444-4444-444444444442', NULL, NULL, 'DUE_DATE_REMINDER', 'SMS', TRUE)
ON CONFLICT (id) DO NOTHING;

-- Customer preferences
INSERT INTO customer_notification_preferences (id, customer_id, channel, is_enabled)
VALUES ('p1111111-1111-1111-1111-111111111111', 'd1e2f3a4-b5c6-7890-def1-234567890abc', 'EMAIL', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO customer_notification_preferences (id, customer_id, channel, is_enabled)
VALUES ('p1111111-1111-1111-1111-111111111112', 'd1e2f3a4-b5c6-7890-def1-234567890abc', 'SMS', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO customer_notification_preferences (id, customer_id, channel, is_enabled)
VALUES ('p2222222-2222-2222-2222-222222222221', 'd2e3f4a5-b6c7-8901-def2-345678901bcd', 'EMAIL', TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO customer_notification_preferences (id, customer_id, channel, is_enabled)
VALUES ('p2222222-2222-2222-2222-222222222222', 'd2e3f4a5-b6c7-8901-def2-345678901bcd', 'SMS', FALSE)
ON CONFLICT (id) DO NOTHING;

