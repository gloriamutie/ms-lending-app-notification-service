package com.glo.lending.notification.service;

import com.glo.lending.notification.model.enums.NotificationChannel;
import com.glo.lending.notification.model.enums.NotificationEventType;
import com.glo.lending.notification.model.enums.NotificationStatus;
import com.glo.lending.notification.repository.entities.CustomerNotificationPreference;
import com.glo.lending.notification.repository.entities.Notification;
import com.glo.lending.notification.repository.entities.NotificationRule;
import com.glo.lending.notification.repository.entities.NotificationTemplate;
import com.glo.lending.notification.repository.repo.CustomerNotificationPreferenceRepository;
import com.glo.lending.notification.repository.repo.NotificationRepository;
import com.glo.lending.notification.repository.repo.NotificationRuleRepository;
import com.glo.lending.notification.repository.repo.NotificationTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Unit Tests")
class NotificationServiceTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationTemplateRepository templateRepository;
    @Mock private NotificationRuleRepository ruleRepository;
    @Mock private CustomerNotificationPreferenceRepository preferenceRepository;

    @InjectMocks private NotificationService notificationService;

    private UUID customerId;
    private UUID loanId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        loanId = UUID.randomUUID();
    }

    @Nested
    @DisplayName("processEvent")
    class ProcessEvent {

        @Test
        @DisplayName("should process event and create notification when rule, preference, and template exist")
        void processEvent_AllConditionsMet_CreatesNotification() {
            // Given
            final NotificationRule rule = new NotificationRule();
            rule.setId(UUID.randomUUID());
            rule.setEventType(NotificationEventType.LOAN_CREATED);
            rule.setChannel(NotificationChannel.EMAIL);
            rule.setIsActive(true);

            final CustomerNotificationPreference pref = new CustomerNotificationPreference();
            pref.setCustomerId(customerId);
            pref.setChannel(NotificationChannel.EMAIL);
            pref.setIsEnabled(true);

            final NotificationTemplate template = new NotificationTemplate();
            template.setId(UUID.randomUUID());
            template.setEventType(NotificationEventType.LOAN_CREATED);
            template.setChannel(NotificationChannel.EMAIL);
            template.setSubjectTemplate("Loan Disbursed - {{productName}}");
            template.setBodyTemplate("Dear {{customerName}}, your loan of KES {{loanAmount}} has been disbursed.");

            final Notification savedNotification = new Notification();
            savedNotification.setId(UUID.randomUUID());
            savedNotification.setCustomerId(customerId);
            savedNotification.setStatus(NotificationStatus.SENT);

            when(ruleRepository.findByEventTypeAndIsActive("LOAN_CREATED", true)).thenReturn(Flux.just(rule));
            when(preferenceRepository.findByCustomerIdAndIsEnabled(customerId, true)).thenReturn(Flux.just(pref));
            when(templateRepository.findByEventTypeAndChannelAndIsActive("LOAN_CREATED", "EMAIL", true))
                    .thenReturn(Mono.just(template));
            when(notificationRepository.save(any(Notification.class))).thenReturn(Mono.just(savedNotification));

            final Map<String, String> variables = Map.of(
                    "customerName", "Jane", "loanAmount", "10000", "productName", "Quick Cash"
            );

            // When & Then
            StepVerifier.create(notificationService.processEvent("LOAN_CREATED", customerId, loanId, variables))
                    .assertNext(n -> {
                        assertNotNull(n.getId());
                        assertEquals(NotificationStatus.SENT, n.getStatus());
                    })
                    .verifyComplete();

            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("should skip notification when customer preference is disabled")
        void processEvent_PreferenceDisabled_SkipsNotification() {
            // Given
            final NotificationRule rule = new NotificationRule();
            rule.setEventType(NotificationEventType.LOAN_CREATED);
            rule.setChannel(NotificationChannel.SMS);
            rule.setIsActive(true);

            when(ruleRepository.findByEventTypeAndIsActive("LOAN_CREATED", true)).thenReturn(Flux.just(rule));
            when(preferenceRepository.findByCustomerIdAndIsEnabled(customerId, true)).thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(notificationService.processEvent("LOAN_CREATED", customerId, loanId, Map.of()))
                    .verifyComplete();

            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("should skip when no matching rules exist")
        void processEvent_NoRules_SkipsNotification() {
            // Given
            when(ruleRepository.findByEventTypeAndIsActive("UNKNOWN_EVENT", true)).thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(notificationService.processEvent("UNKNOWN_EVENT", customerId, loanId, Map.of()))
                    .verifyComplete();

            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("should skip when no template found for channel")
        void processEvent_NoTemplate_SkipsNotification() {
            // Given
            final NotificationRule rule = new NotificationRule();
            rule.setEventType(NotificationEventType.LOAN_CREATED);
            rule.setChannel(NotificationChannel.PUSH);
            rule.setIsActive(true);

            final CustomerNotificationPreference pref = new CustomerNotificationPreference();
            pref.setCustomerId(customerId);
            pref.setChannel(NotificationChannel.PUSH);
            pref.setIsEnabled(true);

            when(ruleRepository.findByEventTypeAndIsActive("LOAN_CREATED", true)).thenReturn(Flux.just(rule));
            when(preferenceRepository.findByCustomerIdAndIsEnabled(customerId, true)).thenReturn(Flux.just(pref));
            when(templateRepository.findByEventTypeAndChannelAndIsActive("LOAN_CREATED", "PUSH", true))
                    .thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(notificationService.processEvent("LOAN_CREATED", customerId, loanId, Map.of()))
                    .verifyComplete();

            verify(notificationRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getNotificationsByCustomerId")
    class GetNotificationsByCustomerId {

        @Test
        @DisplayName("should return notifications for customer")
        void getByCustomerId_HasNotifications_ReturnsFlux() {
            // Given
            final Notification notification = new Notification();
            notification.setId(UUID.randomUUID());
            notification.setCustomerId(customerId);
            when(notificationRepository.findByCustomerId(customerId)).thenReturn(Flux.just(notification));

            // When & Then
            StepVerifier.create(notificationService.getNotificationsByCustomerId(customerId))
                    .assertNext(n -> assertEquals(customerId, n.getCustomerId()))
                    .verifyComplete();
        }

        @Test
        @DisplayName("should return empty when no notifications")
        void getByCustomerId_NoNotifications_ReturnsEmpty() {
            // Given
            when(notificationRepository.findByCustomerId(customerId)).thenReturn(Flux.empty());

            // When & Then
            StepVerifier.create(notificationService.getNotificationsByCustomerId(customerId))
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("getNotificationsByLoanId")
    class GetNotificationsByLoanId {

        @Test
        @DisplayName("should return notifications for loan")
        void getByLoanId_HasNotifications_ReturnsFlux() {
            // Given
            final Notification notification = new Notification();
            notification.setId(UUID.randomUUID());
            notification.setLoanId(loanId);
            when(notificationRepository.findByLoanId(loanId)).thenReturn(Flux.just(notification));

            // When & Then
            StepVerifier.create(notificationService.getNotificationsByLoanId(loanId))
                    .assertNext(n -> assertEquals(loanId, n.getLoanId()))
                    .verifyComplete();
        }
    }
}

