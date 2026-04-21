package com.glo.lending.notification.model.dto;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import com.glo.lending.notification.model.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO representing a sent/queued notification.
 *
 * @param id         unique notification identifier
 * @param customerId target customer
 * @param loanId     associated loan
 * @param eventType  triggering event
 * @param channel    delivery channel
 * @param status     delivery status
 * @param subject    notification subject
 * @param body       notification body
 * @param sentAt     when notification was sent
 * @param createdAt  when notification was created
 */
public record NotificationResponse(
        UUID id,
        UUID customerId,
        UUID loanId,
        NotificationEventType eventType,
        NotificationChannel channel,
        NotificationStatus status,
        String subject,
        String body,
        LocalDateTime sentAt,
        LocalDateTime createdAt
) {
}

