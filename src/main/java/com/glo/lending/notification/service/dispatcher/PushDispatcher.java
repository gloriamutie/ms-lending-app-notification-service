package com.glo.lending.notification.service.dispatcher;

import com.glo.lending.notification.model.enums.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PushDispatcher implements NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(PushDispatcher.class);

    @Override
    public Mono<Void> dispatch(NotificationChannel channel, String recipient, String subject, String body) {
        if (channel != NotificationChannel.PUSH) {
            return Mono.empty();
        }

        // Simulate push delivery until a push provider integration is added.
        log.info("Sending PUSH notification to recipient={}", recipient);
        return Mono.empty();
    }
}

