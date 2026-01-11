package com.ghali.ecommerce.order.kafka;

import com.ghali.ecommerce.common.dto.*;
import com.ghali.ecommerce.order.model.Order;
import com.ghali.ecommerce.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Producer Kafka pour les événements de commande
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-events}")
    private String orderEventsTopic;

    public void publishOrderEvent(String eventType, Order order) {
        log.info("📤 Publishing order event: {} for order: {}", eventType, order.getOrderNumber());

        // Construire shipping address DTO
        ShippingAddressDTO addressDTO = null;
        if (order.getShippingAddress() != null) {
            var addr = order.getShippingAddress();
            addressDTO = ShippingAddressDTO.builder()
                    .fullName(addr.getFullName())
                    .addressLine1(addr.getAddressLine1())
                    .addressLine2(addr.getAddressLine2())
                    .city(addr.getCity())
                    .state(addr.getState())
                    .zipCode(addr.getZipCode())
                    .country(addr.getCountry())
                    .phone(addr.getPhone())
                    .build();
        }

        // Construire order items DTOs
        var itemDTOs = order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        // Construire order DTO
        OrderDTO orderDTO = OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerEmail(order.getCustomerEmail())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .shippingAddress(addressDTO)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        // Créer événement Kafka
        KafkaEvent<OrderDTO> event = KafkaEvent.create(
                eventType,
                "order-service",
                orderDTO,
                order.getCustomerId()
        );

        kafkaTemplate.send(orderEventsTopic, order.getOrderNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Order event published successfully: {}", eventType);
                    } else {
                        log.error("❌ Failed to publish order event: {}", ex.getMessage());
                    }
                });
    }
}
