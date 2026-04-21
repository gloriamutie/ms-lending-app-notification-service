package com.glo.lending.notification.repository.entities;

import com.glo.lending.notification.model.enums.NotificationChannel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;


@Data
@Table("customer_notification_preferences")
public class CustomerNotificationPreference {

    @Id
    private UUID id;
    @Column("customer_id")
    private UUID customerId;
    @Column("channel")
    private NotificationChannel channel;
    @Column("is_enabled")
    private Boolean isEnabled;

}

