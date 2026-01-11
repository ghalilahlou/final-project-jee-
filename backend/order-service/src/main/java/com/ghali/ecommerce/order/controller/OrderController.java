package com.ghali.ecommerce.order.controller;

import com.ghali.ecommerce.common.dto.OrderDTO;
import com.ghali.ecommerce.order.model.Order;
import com.ghali.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * API REST pour la gestion des commandes
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    /**
     * Mes commandes (CUSTOMER)
     */
    @GetMapping("/me")
    public ResponseEntity<Page<Order>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication
    ) {
        String customerId = extractCustomerId(authentication);
        log.info("🛒 GET /api/orders/me for customer: {}", customerId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderService.getOrdersByCustomer(customerId, pageable);

        return ResponseEntity.ok(orders);
    }

    /**
     * Créer une commande (CUSTOMER)
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Valid @RequestBody Order order,
            Authentication authentication
    ) {
        String customerId = extractCustomerId(authentication);
        order.setCustomerId(customerId);

        log.info("✨ POST /api/orders - Creating order for customer: {}", customerId);

        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * Obtenir une commande par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        log.info("🛒 GET /api/orders/{}", id);
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Obtenir une commande par numéro
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<Order> getOrderByNumber(@PathVariable String orderNumber) {
        log.info("🛒 GET /api/orders/number/{}", orderNumber);
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(order);
    }

    // === ENDPOINTS ADMIN ===

    /**
     * Lister toutes les commandes par statut (ADMIN)
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Order>> getOrdersByStatus(
            @PathVariable OrderDTO.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("📊 GET /api/orders/status/{}", status);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderService.getOrdersByStatus(status, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * Confirmer une commande (ADMIN) → Déclenche facturation
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long id) {
        log.info("✅ PUT /api/orders/{}/confirm", id);
        Order order = orderService.confirmOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Expédier une commande (ADMIN)
     */
    @PutMapping("/{id}/ship")
    public ResponseEntity<Order> shipOrder(@PathVariable Long id) {
        log.info("📦 PUT /api/orders/{}/ship", id);
        Order order = orderService.shipOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Marquer comme livrée (ADMIN)
     */
    @PutMapping("/{id}/deliver")
    public ResponseEntity<Order> deliverOrder(@PathVariable Long id) {
        log.info("✨ PUT /api/orders/{}/deliver", id);
        Order order = orderService.deliverOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Annuler une commande (ADMIN)
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long id) {
        log.info("❌ PUT /api/orders/{}/cancel", id);
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order service is running! 🛒");
    }

    private String extractCustomerId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject(); // Keycloak ID
    }
}
