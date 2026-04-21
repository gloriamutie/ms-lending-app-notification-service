package com.glo.lending.notification.repository.repo;

import com.glo.lending.notification.repository.entities.CustomerNotificationPreference;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface CustomerNotificationPreferenceRepository extends ReactiveCrudRepository<CustomerNotificationPreference, UUID> {

    Flux<CustomerNotificationPreference> findByCustomerId(UUID customerId);

    Flux<CustomerNotificationPreference> findByCustomerIdAndIsEnabled(UUID customerId, Boolean isEnabled);
}

