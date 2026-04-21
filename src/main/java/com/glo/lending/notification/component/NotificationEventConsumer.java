package com.glo.lending.notification.component;

import com.glo.lending.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka consumer that listens for loan and customer lifecycle events
 * and triggers the notification processing pipeline.
 */
@Component
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final NotificationService notificationService;

    public NotificationEventConsumer(final NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "lending.loan.events", groupId = "notification-service-group")
    public void handleLoanEvent(final Map<String, Object> event) {
        log.info("Received loan event: {}", event.get("eventType"));
        processEvent(event);
    }

    @KafkaListener(topics = "lending.customer.events", groupId = "notification-service-group")
    public void handleCustomerEvent(final Map<String, Object> event) {
        log.info("Received customer event: {}", event.get("eventType"));
        processEvent(event);
    }

    private void processEvent(final Map<String, Object> event) {
        try {
            final String eventType = String.valueOf(event.get("eventType"));
            final UUID customerId = UUID.fromString(String.valueOf(event.get("customerId")));
            final UUID loanId = event.containsKey("loanId") ? UUID.fromString(String.valueOf(event.get("loanId"))) : null;

            final Map<String, String> variables = new HashMap<>();
            event.forEach((k, v) -> {
                if (v != null) variables.put(k, String.valueOf(v));
            });

            notificationService.processEvent(eventType, customerId, loanId, variables)
                    .subscribe(
                            n -> log.debug("Notification dispatched: id={}", n.getId()),
                            error -> log.error("Failed to process notification event: {}", error.getMessage())
                    );
        } catch (final Exception e) {
            log.error("Error parsing notification event: {}", e.getMessage(), e);
        }
    }
}

