package com.glo.lending.notification.model.dto;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating or updating a notification template.
 *
 * @param eventType       the event this template handles
 * @param channel         the delivery channel
 * @param subjectTemplate subject line with {{variable}} placeholders
 * @param bodyTemplate    body content with {{variable}} placeholders
 * @param isActive        whether the template is active
 */
public record TemplateRequest(
        @NotNull(message = "Event type is required")
        NotificationEventType eventType,

        @NotNull(message = "Channel is required")
        NotificationChannel channel,

        @NotBlank(message = "Subject template is required")
        String subjectTemplate,

        @NotBlank(message = "Body template is required")
        String bodyTemplate,

        @NotNull(message = "Active flag is required")
        Boolean isActive
) {
}

