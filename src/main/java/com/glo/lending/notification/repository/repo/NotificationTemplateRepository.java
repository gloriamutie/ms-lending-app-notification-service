package com.glo.lending.notification.repository.repo;

import com.glo.lending.notification.repository.entities.NotificationTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface NotificationTemplateRepository extends ReactiveCrudRepository<NotificationTemplate, UUID> {

    Mono<NotificationTemplate> findByEventTypeAndChannelAndIsActive(String eventType, String channel, Boolean isActive);
}

