package com.ghali.ecommerce.billing.kafka;

import com.ghali.ecommerce.billing.service.InvoiceService;
import com.ghali.ecommerce.common.dto.KafkaEvent;
import com.ghali.ecommerce.common.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer Kafka pour les événements de commande
 * Génère automatiquement des factures quand une commande est confirmée
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final InvoiceService invoiceService;

    @KafkaListener(
        topics = "${kafka.topics.order-events}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(KafkaEvent<OrderDTO> event) {
        log.info("📬 Received order event: {} - Order: {}", 
            event.getEventType(), event.getPayload().getOrderNumber());
        
        try {
            OrderDTO order = event.getPayload();
            
            switch (event.getEventType()) {
                case "ORDER_CONFIRMED":
                    handleOrderConfirmed(order);
                    break;
                    
                case "ORDER_CANCELLED":
                    handleOrderCancelled(order);
                    break;
                    
                default:
                    log.debug("Event type {} not handled by billing service", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    /**
     * Gérer la confirmation de commande - Créer une facture
     */
    private void handleOrderConfirmed(OrderDTO order) {
        log.info("💰 Processing ORDER_CONFIRMED for order: {}", order.getOrderNumber());
        
        try {
            invoiceService.createInvoiceFromOrder(order);
            log.info("✅ Invoice created successfully for order: {}", order.getOrderNumber());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already exists")) {
                log.warn("⚠️ Invoice already exists for order: {}", order.getOrderNumber());
            } else {
                throw e;
            }
        }
    }

    /**
     * Gérer l'annulation de commande - Annuler la facture
     */
    private void handleOrderCancelled(OrderDTO order) {
        log.info("❌ Processing ORDER_CANCELLED for order: {}", order.getOrderNumber());
        
        try {
            var invoice = invoiceService.getInvoiceByOrderId(order.getId());
            
            if (invoice.getStatus() != com.ghali.ecommerce.billing.model.Invoice.InvoiceStatus.PAID) {
                invoiceService.updateInvoiceStatus(
                    invoice.getId(), 
                    com.ghali.ecommerce.billing.model.Invoice.InvoiceStatus.CANCELLED
                );
                log.info("✅ Invoice cancelled for order: {}", order.getOrderNumber());
            } else {
                log.warn("⚠️ Cannot cancel paid invoice for order: {}", order.getOrderNumber());
            }
        } catch (RuntimeException e) {
            log.warn("⚠️ No invoice found for cancelled order: {}", order.getOrderNumber());
        }
    }
}
