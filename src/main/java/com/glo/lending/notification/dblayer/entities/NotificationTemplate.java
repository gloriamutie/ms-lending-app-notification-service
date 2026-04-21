package com.glo.lending.notification.dblayer.entities;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("notification_templates")
public class NotificationTemplate {

    @Id
    private UUID id;
    @Column("event_type")
    private NotificationEventType eventType;
    @Column("channel")
    private NotificationChannel channel;
    @Column("subject_template")
    private String subjectTemplate;
    @Column("body_template")
    private String bodyTemplate;
    @Column("is_active")
    private Boolean isActive;

}

