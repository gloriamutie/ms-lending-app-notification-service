package com.glo.lending.notification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka consumer configuration for the Notification Service.
 * <p>
 * <b>Partitioning Strategy:</b> The notification consumer group uses
 * {@code concurrency=3} to process 3 partitions in parallel (out of the
 * 6 created by the Loan Service). This allows horizontal scaling:
 * deploying 2 instances of the notification service covers all 6 partitions.
 * </p>
 * <p>
 * Since events are partitioned by customerId, each consumer thread processes
 * events for a distinct set of customers, avoiding race conditions in
 * notification deduplication and template resolution.
 * </p>
 */
//TODO
@Configuration
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    /**
     * Consumer factory configured for JSON deserialization with trusted packages.
     *
     * @return a {@link ConsumerFactory} for String keys and Object values
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Enable cooperative rebalancing for smoother partition reassignment
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.glo.lending.*,java.util");
        return new DefaultKafkaConsumerFactory<>(props);
    }
    //TODO

    /**
     * Kafka listener container factory with concurrency=3 for parallel partition processing.
     * Uses MANUAL_IMMEDIATE ack mode to ensure at-least-once delivery.
     *
     * @return a {@link ConcurrentKafkaListenerContainerFactory} for notification consumers
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        log.info("Configuring Kafka consumer: groupId={}, concurrency=3, ackMode=MANUAL_IMMEDIATE", groupId);
        final ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // 3 concurrent consumers to handle 3 of 6 partitions per instance
        factory.setConcurrency(3);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}

