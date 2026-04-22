package com.glo.lending.notification.model.dto;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import com.glo.lending.notification.model.enums.NotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationResponse {
   private UUID id;
    private UUID customerId;
    private UUID loanId;
    private NotificationEventType eventType;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String subject;
    private  String body;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}

