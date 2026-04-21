package com.glo.lending.notification.repository.entities;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import com.glo.lending.notification.model.enums.NotificationStatus;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("notifications")
public class Notification {

    @Id
    private UUID id;

    @Column("customer_id")
    private UUID customerId;

    @Column("loan_id")
    private UUID loanId;

    @Column("event_type")
    private NotificationEventType eventType;

    @Column("channel")
    private NotificationChannel channel;

    @Column("status")
    private NotificationStatus status;

    @Column("subject")
    private String subject;

    @Column("body")
    private String body;

    @Column("sent_at")
    private LocalDateTime sentAt;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

}

