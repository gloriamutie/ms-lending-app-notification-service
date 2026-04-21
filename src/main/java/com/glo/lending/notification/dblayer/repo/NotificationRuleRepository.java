package com.glo.lending.notification.dblayer.repo;

import com.glo.lending.notification.dblayer.entities.NotificationRule;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface NotificationRuleRepository extends ReactiveCrudRepository<NotificationRule, UUID> {

    Flux<NotificationRule> findByEventTypeAndIsActive(String eventType, Boolean isActive);

    Flux<NotificationRule> findByProductIdAndEventTypeAndIsActive(UUID productId, String eventType, Boolean isActive);
}

