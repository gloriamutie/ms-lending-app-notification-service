package com.glo.lending.notification.service.dispatcher;

import com.glo.lending.notification.model.enums.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SmsDispatcher implements NotificationDispatcher {

    @Override
    public Mono<Void> dispatch(NotificationChannel channel, String recipient, String subject, String body) {
        if (channel != NotificationChannel.SMS) return Mono.empty();

        //webclinet call to sms service
        System.out.println("Sending SMS to " + recipient);

        return Mono.empty();
    }
}
