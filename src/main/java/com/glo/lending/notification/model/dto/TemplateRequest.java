package com.glo.lending.notification.model.dto;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class TemplateRequest{
        @NotNull(message = "Event type is required")
        private NotificationEventType eventType;

        @NotNull(message = "Channel is required")
        private NotificationChannel channel;

        @NotBlank(message = "Subject template is required")
        private String subjectTemplate;

        @NotBlank(message = "Body template is required")
        private String bodyTemplate;

        @NotNull(message = "Active flag is required")
        private  Boolean isActive;
}

