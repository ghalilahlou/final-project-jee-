package com.ghali.ecommerce.product.kafka;

import com.ghali.ecommerce.common.dto.KafkaEvent;
import com.ghali.ecommerce.common.dto.ProductDTO;
import com.ghali.ecommerce.product.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Producer Kafka pour les événements produits
 * Inspiré du projet Kafka original
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.product-events}")
    private String productEventsTopic;

    public void publishProductEvent(String eventType, Product product) {
        log.info("📤 Publishing product event: {} for product: {}", eventType, product.getSku());

        ProductDTO productDTO = ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .active(product.getActive())
                .build();

        KafkaEvent<ProductDTO> event = KafkaEvent.create(
                eventType,
                "product-service",
                productDTO,
                "system"
        );

        kafkaTemplate.send(productEventsTopic, product.getSku(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Product event published successfully: {}", eventType);
                    } else {
                        log.error("❌ Failed to publish product event: {}", ex.getMessage());
                    }
                });
    }
}
