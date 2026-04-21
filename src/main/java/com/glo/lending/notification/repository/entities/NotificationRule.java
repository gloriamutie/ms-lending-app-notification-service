package com.glo.lending.notification.repository.entities;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("notification_rules")
public class NotificationRule {

    @Id
    private UUID id;

    @Column("product_id")
    private UUID productId;

    @Column("customer_segment")
    private String customerSegment;

    @Column("event_type")
    private NotificationEventType eventType;

    @Column("channel")
    private NotificationChannel channel;

    @Column("is_active")
    private Boolean isActive;


}

