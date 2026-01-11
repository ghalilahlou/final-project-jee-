package com.ghali.ecommerce.customer.controller;

import com.ghali.ecommerce.customer.model.Customer;
import com.ghali.ecommerce.customer.model.CustomerAddress;
import com.ghali.ecommerce.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST pour la gestion des clients
 * Accessible via Gateway avec authentification Keycloak
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Obtenir le profil du client connecté
     */
    @GetMapping("/me")
    public ResponseEntity<Customer> getMyProfile(Authentication authentication) {
        String keycloakId = extractKeycloakId(authentication);
        log.info("👤 GET /api/customers/me - Keycloak ID: {}", keycloakId);
        
        Customer customer = customerService.getCustomerByKeycloakId(keycloakId);
        return ResponseEntity.ok(customer);
    }

    /**
     * Mettre à jour le profil du client connecté
     */
    @PutMapping("/me")
    public ResponseEntity<Customer> updateMyProfile(
            @Valid @RequestBody Customer customerData,
            Authentication authentication
    ) {
        String keycloakId = extractKeycloakId(authentication);
        log.info("🔄 PUT /api/customers/me - Updating profile");
        
        Customer updatedCustomer = customerService.updateCustomer(keycloakId, customerData);
        return ResponseEntity.ok(updatedCustomer);
    }

    /**
     * Obtenir les adresses du client connecté
     */
    @GetMapping("/me/addresses")
    public ResponseEntity<List<CustomerAddress>> getMyAddresses(Authentication authentication) {
        String keycloakId = extractKeycloakId(authentication);
        log.info("📍 GET /api/customers/me/addresses");
        
        Customer customer = customerService.getCustomerByKeycloakId(keycloakId);
        List<CustomerAddress> addresses = customerService.getCustomerAddresses(customer.getId());
        return ResponseEntity.ok(addresses);
    }

    /**
     * Ajouter une adresse pour le client connecté
     */
    @PostMapping("/me/addresses")
    public ResponseEntity<CustomerAddress> addAddress(
            @Valid @RequestBody CustomerAddress address,
            Authentication authentication
    ) {
        String keycloakId = extractKeycloakId(authentication);
        log.info("📍 POST /api/customers/me/addresses - Adding address");
        
        Customer customer = customerService.getCustomerByKeycloakId(keycloakId);
        CustomerAddress savedAddress = customerService.addAddress(customer.getId(), address);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
    }

    /**
     * Définir une adresse par défaut
     */
    @PutMapping("/me/addresses/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(
            @PathVariable Long addressId,
            Authentication authentication
    ) {
        String keycloakId = extractKeycloakId(authentication);
        log.info("📍 PUT /api/customers/me/addresses/{}/default", addressId);
        
        Customer customer = customerService.getCustomerByKeycloakId(keycloakId);
        customerService.setDefaultAddress(customer.getId(), addressId);
        return ResponseEntity.ok().build();
    }

    /**
     * Supprimer une adresse
     */
    @DeleteMapping("/me/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        log.info("🗑️ DELETE /api/customers/me/addresses/{}", addressId);
        customerService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    // === ENDPOINTS ADMIN UNIQUEMENT ===

    /**
     * Rechercher des clients (ADMIN)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String keyword) {
        log.info("🔍 GET /api/customers/search?keyword={}", keyword);
        List<Customer> customers = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(customers);
    }

    /**
     * Obtenir les meilleurs clients (ADMIN)
     */
    @GetMapping("/top")
    public ResponseEntity<List<Customer>> getTopCustomers(
            @RequestParam(defaultValue = "5") int minOrders
    ) {
        log.info("🏆 GET /api/customers/top");
        List<Customer> customers = customerService.getTopCustomers(minOrders);
        return ResponseEntity.ok(customers);
    }

    /**
     * Obtenir un client par ID (ADMIN)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        log.info("👤 GET /api/customers/{}", id);
        Customer customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Customer service is running! 👥");
    }

    /**
     * Extraire le Keycloak ID du JWT
     */
    private String extractKeycloakId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject(); // Le "sub" claim contient le Keycloak user ID
    }
}
