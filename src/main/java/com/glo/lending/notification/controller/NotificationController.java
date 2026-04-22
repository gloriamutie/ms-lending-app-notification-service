package com.glo.lending.notification.controller;

import com.glo.lending.notification.dblayer.entities.Notification;
import com.glo.lending.notification.dblayer.entities.NotificationTemplate;
import com.glo.lending.notification.dblayer.repo.NotificationTemplateRepository;
import com.glo.lending.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST controller for querying notifications and managing templates.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final NotificationTemplateRepository templateRepository;

    @GetMapping("/customer/{customerId}")
    public Mono<ResponseEntity<Flux<Notification>>> getByCustomerId(@PathVariable  UUID customerId) {
        log.info("GET /api/v1/notifications/customer/{}", customerId);
        return Mono.just(ResponseEntity.ok(notificationService.getNotificationsByCustomerId(customerId)));
    }

    @GetMapping("/loan/{loanId}")
    public Mono<ResponseEntity<Flux<Notification>>> getByLoan(@PathVariable  UUID loanId) {
        log.info("GET /api/v1/notifications/loan/{}", loanId);
        return Mono.just(ResponseEntity.ok(notificationService.getNotificationsByLoanId(loanId)));
    }

    @GetMapping("/templates")
    public Mono<ResponseEntity<Flux<NotificationTemplate>>> getAllTemplates() {
        log.info("GET /api/v1/notifications/templates");
        return Mono.just(ResponseEntity.ok(templateRepository.findAll()));
    }
}

