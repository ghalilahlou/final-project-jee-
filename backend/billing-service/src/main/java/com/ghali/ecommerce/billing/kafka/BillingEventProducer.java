package com.ghali.ecommerce.billing.kafka;

import com.ghali.ecommerce.billing.model.Invoice;
import com.ghali.ecommerce.common.dto.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Producer Kafka pour les événements de facturation
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BillingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.billing-events}")
    private String billingEventsTopic;

    public void publishInvoiceEvent(String eventType, Invoice invoice) {
        log.info("📤 Publishing billing event: {} for invoice: {}", eventType, invoice.getInvoiceNumber());

        Map<String, Object> invoiceData = new HashMap<>();
        invoiceData.put("invoiceId", invoice.getId());
        invoiceData.put("invoiceNumber", invoice.getInvoiceNumber());
        invoiceData.put("orderId", invoice.getOrderId());
        invoiceData.put("orderNumber", invoice.getOrderNumber());
        invoiceData.put("customerId", invoice.getCustomerId());
        invoiceData.put("totalAmount", invoice.getTotalAmount());
        invoiceData.put("status", invoice.getStatus().toString());

        KafkaEvent<Map<String, Object>> event = KafkaEvent.create(
                eventType,
                "billing-service",
                invoiceData,
                invoice.getCustomerId()
        );

        kafkaTemplate.send(billingEventsTopic, invoice.getInvoiceNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Billing event published successfully: {}", eventType);
                    } else {
                        log.error("❌ Failed to publish billing event: {}", ex.getMessage());
                    }
                });
    }
}
