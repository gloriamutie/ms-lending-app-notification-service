package com.glo.lending.notification.model.dto;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import lombok.Builder;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class NotificationEvent {
    private NotificationEventType eventType;
    private UUID customerId;
    private UUID loanId;
    private String customerName;
    private  String customerEmail;
    private String customerPhone;
    private BigDecimal loanAmount;
    private LocalDate dueDate;
    private String productName;
    private  NotificationChannel channel;

}

