package com.ghali.ecommerce.notification.kafka;

import com.ghali.ecommerce.common.dto.KafkaEvent;
import com.ghali.ecommerce.common.dto.OrderDTO;
import com.ghali.ecommerce.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer Kafka pour les événements de commande
 * Pattern inspiré du projet Kafka original
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final EmailService emailService;

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
                case "ORDER_CREATED":
                    handleOrderCreated(order);
                    break;
                case "ORDER_CONFIRMED":
                    handleOrderConfirmed(order);
                    break;
                case "ORDER_SHIPPED":
                    handleOrderShipped(order);
                    break;
                case "ORDER_DELIVERED":
                    handleOrderDelivered(order);
                    break;
                case "ORDER_CANCELLED":
                    handleOrderCancelled(order);
                    break;
                default:
                    log.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            log.error("Error processing order event: {}", e.getMessage(), e);
        }
    }

    private void handleOrderCreated(OrderDTO order) {
        log.info("🎉 Processing ORDER_CREATED for: {}", order.getOrderNumber());
        String subject = "Confirmation de commande - " + order.getOrderNumber();
        String body = buildOrderCreatedEmail(order);
        emailService.sendEmail(order.getCustomerEmail(), subject, body);
    }

    private void handleOrderConfirmed(OrderDTO order) {
        log.info("✅ Processing ORDER_CONFIRMED for: {}", order.getOrderNumber());
        String subject = "Commande confirmée - " + order.getOrderNumber();
        String body = "Votre commande a été confirmée et est en cours de traitement.";
        emailService.sendEmail(order.getCustomerEmail(), subject, body);
    }

    private void handleOrderShipped(OrderDTO order) {
        log.info("📦 Processing ORDER_SHIPPED for: {}", order.getOrderNumber());
        String subject = "Commande expédiée - " + order.getOrderNumber();
        String body = "Votre commande a été expédiée et sera bientôt livrée.";
        emailService.sendEmail(order.getCustomerEmail(), subject, body);
    }

    private void handleOrderDelivered(OrderDTO order) {
        log.info("✨ Processing ORDER_DELIVERED for: {}", order.getOrderNumber());
        String subject = "Commande livrée - " + order.getOrderNumber();
        String body = "Votre commande a été livrée. Merci pour votre achat !";
        emailService.sendEmail(order.getCustomerEmail(), subject, body);
    }

    private void handleOrderCancelled(OrderDTO order) {
        log.info("❌ Processing ORDER_CANCELLED for: {}", order.getOrderNumber());
        String subject = "Commande annulée - " + order.getOrderNumber();
        String body = "Votre commande a été annulée.";
        emailService.sendEmail(order.getCustomerEmail(), subject, body);
    }

    private String buildOrderCreatedEmail(OrderDTO order) {
        return String.format("""
            Bonjour,
            
            Nous avons bien reçu votre commande #%s.
            
            Montant total: %.2f MAD
            Nombre d'articles: %d
            
            Nous vous tiendrons informé de l'évolution de votre commande.
            
            Merci pour votre confiance !
            
            L'équipe E-Commerce
            """, 
            order.getOrderNumber(), 
            order.getTotalAmount(), 
            order.getItems().size()
        );
    }
}
