package com.glo.lending.notification.service.dispatcher;

import com.glo.lending.notification.model.enums.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class EmailDispatcher implements NotificationDispatcher {

    @Override
    public Mono<Void> dispatch(NotificationChannel channel, String recipient, String subject, String body) {
        if (channel != NotificationChannel.EMAIL) return Mono.empty();

        // call email service API to send email
        //webclient call to email service -- not ready - just simulating with print statement
        System.out.println("Sending EMAIL to " + recipient);

        return Mono.empty();
    }
}
