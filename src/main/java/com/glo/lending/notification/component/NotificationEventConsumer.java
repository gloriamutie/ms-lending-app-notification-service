package com.glo.lending.notification.component;

import com.glo.lending.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Kafka consumer that listens for loan and customer lifecycle events
 * and triggers the notification processing pipeline.
 */
@RequiredArgsConstructor
@Component
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final NotificationService notificationService;


    @KafkaListener(
            topics = "${app.kafka.topic.loan-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleLoanEvent( Map<String, Object> event) {
        log.info("Received loan event: {}", event.get("eventType"));
        processEvent(event);
    }

    @KafkaListener(
            topics = "${app.kafka.topic.customer-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleCustomerEvent( Map<String, Object> event) {
        log.info("Received customer event: {}", event.get("eventType"));
        processEvent(event);
    }

    private void processEvent( Map<String, Object> event) {
        final String eventType = String.valueOf(event.get("eventType"));
        final Optional<UUID> customerId = parseUuid(event.get("customerId"));
        if (customerId.isEmpty()) {
            log.warn("Skipping event {} due to invalid customerId: {}", eventType, event.get("customerId"));
            return;
        }

        final UUID loanId = parseUuid(event.get("loanId")).orElse(null);

        final Map<String, String> variables = new HashMap<>();
        event.forEach((k, v) -> {
            if (v != null) {
                variables.put(k, String.valueOf(v));
            }
        });

        notificationService.processEvent(eventType, customerId.get(), loanId, variables)
                .subscribe(
                        n -> log.debug("Notification dispatched: id={}", n.getId()),
                        error -> log.error("Failed to process notification event: {}", error.getMessage())
                );
    }

    private Optional<UUID> parseUuid(final Object rawValue) {
        if (rawValue == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(String.valueOf(rawValue)));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}

