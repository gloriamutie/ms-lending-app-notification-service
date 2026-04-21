package com.glo.lending.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Notification Service microservice.
 * Consumes Kafka events and dispatches notifications via configured channels.
 */
@SpringBootApplication
public class NotificationServiceApplication {

    public static void main(final String[] args) {

        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}

