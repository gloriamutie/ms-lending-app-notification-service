package com.glo.lending.notification.dblayer.repo;

import com.glo.lending.notification.dblayer.entities.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, UUID> {

    Flux<Notification> findByCustomerId(UUID customerId);

    Flux<Notification> findByLoanId(UUID loanId);

    Flux<Notification> findByStatus(String status);
}

