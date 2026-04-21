package com.glo.lending.notification.service;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import com.glo.lending.notification.model.enums.NotificationStatus;
import com.glo.lending.notification.repository.entities.Notification;
import com.glo.lending.notification.repository.entities.NotificationTemplate;
import com.glo.lending.notification.repository.repo.CustomerNotificationPreferenceRepository;
import com.glo.lending.notification.repository.repo.NotificationRepository;
import com.glo.lending.notification.repository.repo.NotificationRuleRepository;
import com.glo.lending.notification.repository.repo.NotificationTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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

    /**
     * Processes a notification event: resolves rules, checks preferences, renders template, saves notification.
     *
     * @param eventType  the event type
     * @param customerId the target customer
     * @param loanId     associated loan (nullable)
     * @param variables  template variables for substitution
     * @return a {@link Flux} of saved notification records
     */
    public Flux<Notification> processEvent( String eventType,  UUID customerId,  UUID loanId, Map<String, String> variables) {
        log.info("Processing notification event: type={}, customerId={}", eventType, customerId);

        return ruleRepository.findByEventTypeAndIsActive(eventType, true)
                .flatMap(rule -> {
                     NotificationChannel channel = rule.getChannel();
                     return preferenceRepository.findByCustomerIdAndIsEnabled(customerId, true)
                            .filter(pref -> pref.getChannel() == channel)
                            .next()
                            .flatMap(pref -> templateRepository.findByEventTypeAndChannelAndIsActive(eventType, channel.name(), true)
                                    .flatMap(template -> {
                                        final String subject = substituteVariables(template.getSubjectTemplate(), variables);
                                        final String body = substituteVariables(template.getBodyTemplate(), variables);
                                        return saveNotification(customerId, loanId, eventType, channel, subject, body);
                                    })
                            );
                })
                .doOnComplete(() -> log.info("Notification processing complete for event: {}, customer: {}", eventType, customerId));
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

    private String substituteVariables(final String template, final Map<String, String> variables) {
        String result = template;
        for (final Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}

