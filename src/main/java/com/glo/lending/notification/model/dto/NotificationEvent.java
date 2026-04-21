package com.glo.lending.notification.model.dto;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Kafka event DTO consumed by the notification service.
 * Published by loan-service and customer-service when notable events occur.
 *
 * @param eventType      the event that triggered this notification
 * @param customerId     target customer
 * @param loanId         associated loan (nullable)
 * @param customerName   customer display name
 * @param customerEmail  customer email
 * @param customerPhone  customer phone
 * @param loanAmount     relevant amount
 * @param dueDate        relevant due date
 * @param productName    loan product name
 * @param channel        preferred channel override (nullable — uses rules if null)
 */
public record NotificationEvent(
        NotificationEventType eventType,
        UUID customerId,
        UUID loanId,
        String customerName,
        String customerEmail,
        String customerPhone,
        BigDecimal loanAmount,
        LocalDate dueDate,
        String productName,
        NotificationChannel channel
) {
}

