package com.glo.lending.notification.service;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import com.glo.lending.notification.model.enums.NotificationStatus;
import com.glo.lending.notification.dblayer.entities.Notification;
import com.glo.lending.notification.dblayer.repo.CustomerNotificationPreferenceRepository;
import com.glo.lending.notification.dblayer.repo.NotificationRepository;
import com.glo.lending.notification.dblayer.repo.NotificationRuleRepository;
import com.glo.lending.notification.dblayer.repo.NotificationTemplateRepository;
import com.glo.lending.notification.service.dispatcher.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Core notification processing service.
 * Resolves rules, checks preferences, renders templates, and dispatches notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationRuleRepository ruleRepository;
    private final CustomerNotificationPreferenceRepository preferenceRepository;
    private final List<NotificationDispatcher> dispatchers;


    public Flux<Notification> processEvent( String eventType,  UUID customerId,  UUID loanId, Map<String, String> variables) {
        log.info("Processing notification event: type={}, customerId={}", eventType, customerId);

        return ruleRepository.findByEventTypeAndIsActive(eventType, true)
                .flatMap(notificationRule ->  {
                     NotificationChannel channel = notificationRule.getChannel();
                     return preferenceRepository.findByCustomerIdAndIsEnabled(customerId, true)
                            .filter(pref -> pref.getChannel() == channel)
                            .next()
                            .flatMap(pref -> templateRepository.findByEventTypeAndChannelAndIsActive(eventType, channel.name(), true)
                                    .flatMap(template -> {
                                         String subject = substituteVariables(template.getSubjectTemplate(), variables);
                                         String body = substituteVariables(template.getBodyTemplate(), variables);

                                        String recipient = resolveRecipient(channel, variables);

                                        return Flux.fromIterable(dispatchers)
                                                .flatMap(dispatcher -> dispatcher.dispatch(channel, recipient, subject, body))
                                                .then(saveNotification(customerId, loanId, eventType, channel, subject, body));

                                    })
                            );
                })
                .doOnComplete(() -> log.info("Notification processing complete for event: {}, customer: {}", eventType, customerId));
    }

    private String resolveRecipient(NotificationChannel channel, Map<String, String> variables) {
        return switch (channel) {
            case EMAIL -> variables.get("customerEmail");
            case SMS -> variables.get("customerPhone");
            case PUSH -> firstNonBlank(variables, "customerPushToken", "pushToken", "deviceToken");
            default -> throw new IllegalArgumentException("Unsupported channel: " + channel);
        };
    }

    private String firstNonBlank(final Map<String, String> variables, final String... keys) {
        for (final String key : keys) {
            final String value = variables.get(key);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    /**
     * Retrieves all notifications for a customer.
     */
    public Flux<Notification> getNotificationsByCustomerId(final UUID customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }

    /**
     * Retrieves all notifications for a loan.
     */
    public Flux<Notification> getNotificationsByLoanId(final UUID loanId) {
        return notificationRepository.findByLoanId(loanId);
    }

    private Mono<Notification> saveNotification(final UUID customerId, final UUID loanId,
                                                 final String eventType, final NotificationChannel channel,
                                                 final String subject, final String body) {
        final Notification notification = new Notification();
        notification.setCustomerId(customerId);
        notification.setLoanId(loanId);
        notification.setEventType(NotificationEventType.valueOf(eventType));
        notification.setChannel(channel);
        notification.setStatus(NotificationStatus.SENT);
        notification.setSubject(subject);
        notification.setBody(body);
        notification.setSentAt(LocalDateTime.now());
        notification.setCreatedAt(LocalDateTime.now());

        log.info("Dispatching {} notification via {} to customer {}", eventType, channel, customerId);
        return notificationRepository.save(notification)
                .doOnSuccess(n -> log.info("Notification saved: id={}, channel={}", n.getId(), channel));
    }

    private String substituteVariables( String template,  Map<String, String> variables) {
        String result = template;
        for ( Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}

