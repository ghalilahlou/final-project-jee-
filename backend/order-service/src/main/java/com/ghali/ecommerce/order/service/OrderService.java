package com.ghali.ecommerce.order.service;

import com.ghali.ecommerce.common.dto.OrderDTO;
import com.ghali.ecommerce.order.kafka.OrderEventProducer;
import com.ghali.ecommerce.order.model.Order;
import com.ghali.ecommerce.order.model.OrderItem;
import com.ghali.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;

/**
 * Service de gestion des commandes
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found with number: " + orderNumber));
    }

    public Page<Order> getOrdersByCustomer(String customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    public Page<Order> getOrdersByStatus(OrderDTO.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Transactional
    public Order createOrder(Order order) {
        log.info("🛒 Creating new order for customer: {}", order.getCustomerId());

        // Générer numéro de commande
        order.setOrderNumber(generateOrderNumber());

        // Calculer montant total
        BigDecimal total = order.getItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        // Publier événement ORDER_CREATED
        orderEventProducer.publishOrderEvent("ORDER_CREATED", savedOrder);

        log.info("✅ Order created: {}", savedOrder.getOrderNumber());
        return savedOrder;
    }

    @Transactional
    public Order confirmOrder(Long orderId) {
        log.info("✅ Confirming order: {}", orderId);
        Order order = getOrderById(orderId);

        order.confirmOrder();
        Order confirmedOrder = orderRepository.save(order);

        // Publier événement ORDER_CONFIRMED → déclenche facturation
        orderEventProducer.publishOrderEvent("ORDER_CONFIRMED", confirmedOrder);

        log.info("✅ Order confirmed and invoice will be generated: {}", confirmedOrder.getOrderNumber());
        return confirmedOrder;
    }

    @Transactional
    public Order shipOrder(Long orderId) {
        log.info("📦 Shipping order: {}", orderId);
        Order order = getOrderById(orderId);

        order.shipOrder();
        Order shippedOrder = orderRepository.save(order);

        // Publier événement ORDER_SHIPPED
        orderEventProducer.publishOrderEvent("ORDER_SHIPPED", shippedOrder);

        log.info("✅ Order shipped: {}", shippedOrder.getOrderNumber());
        return shippedOrder;
    }

    @Transactional
    public Order deliverOrder(Long orderId) {
        log.info("✨ Delivering order: {}", orderId);
        Order order = getOrderById(orderId);

        order.deliverOrder();
        Order deliveredOrder = orderRepository.save(order);

        // Publier événement ORDER_DELIVERED
        orderEventProducer.publishOrderEvent("ORDER_DELIVERED", deliveredOrder);

        log.info("✅ Order delivered: {}", deliveredOrder.getOrderNumber());
        return deliveredOrder;
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        log.info("❌ Cancelling order: {}", orderId);
        Order order = getOrderById(orderId);

        order.cancelOrder();
        Order cancelledOrder = orderRepository.save(order);

        // Publier événement ORDER_CANCELLED
        orderEventProducer.publishOrderEvent("ORDER_CANCELLED", cancelledOrder);

        log.info("✅ Order cancelled: {}", cancelledOrder.getOrderNumber());
        return cancelledOrder;
    }

    private String generateOrderNumber() {
        String year = String.valueOf(Year.now().getValue());
        long count = orderRepository.count() + 1;
        return String.format("ORD-%s-%06d", year, count);
    }
}
