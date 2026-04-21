package com.glo.lending.notification.service.dispatcher;

import com.glo.lending.notification.model.enums.NotificationChannel;
import reactor.core.publisher.Mono;

public interface NotificationDispatcher {
    Mono<Void> dispatch(NotificationChannel channel, String recipient, String subject, String body);

}
