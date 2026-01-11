package com.ghali.ecommerce.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO générique pour les événements Kafka
 * Inspiré du projet Kafka original
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaEvent<T> {
    
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private String source;
    private T payload;
    private String userId;
    
    public static <T> KafkaEvent<T> create(String eventType, String source, T payload, String userId) {
        return KafkaEvent.<T>builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .eventType(eventType)
                .timestamp(LocalDateTime.now())
                .source(source)
                .payload(payload)
                .userId(userId)
                .build();
    }
}
