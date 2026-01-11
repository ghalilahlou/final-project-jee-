package com.ghali.ecommerce.user.controller;

import com.ghali.ecommerce.user.model.User;
import com.ghali.ecommerce.user.service.UserService;
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
 * API REST pour la gestion des utilisateurs (ADMIN, VENDOR)
 * Accessible uniquement aux ADMIN via Gateway
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * Obtenir mon profil utilisateur
     */
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        String keycloakId = extractKeycloakId(authentication);
        log.info("👤 GET /api/users/me - Keycloak ID: {}", keycloakId);
        
        User user = userService.getUserByKeycloakId(keycloakId);
        return ResponseEntity.ok(user);
    }

    /**
     * Mettre à jour mon profil
     */
    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(
            @Valid @RequestBody User userData,
            Authentication authentication
    ) {
        String keycloakId = extractKeycloakId(authentication);
        log.info("🔄 PUT /api/users/me - Updating profile");
        
        User updatedUser = userService.updateUser(keycloakId, userData);
        return ResponseEntity.ok(updatedUser);
    }

    // === ENDPOINTS ADMIN UNIQUEMENT ===

    /**
     * Lister tous les utilisateurs (ADMIN)
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("👥 GET /api/users - Listing all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Rechercher des utilisateurs (ADMIN)
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        log.info("🔍 GET /api/users/search?keyword={}", keyword);
        List<User> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    /**
     * Obtenir les utilisateurs par rôle (ADMIN)
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable User.UserRole role) {
        log.info("👥 GET /api/users/role/{}", role);
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Obtenir un utilisateur par ID (ADMIN)
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("👤 GET /api/users/{}", id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Créer un nouvel utilisateur (ADMIN)
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("✨ POST /api/users - Creating: {}", user.getUsername());
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * Ajouter un rôle à un utilisateur (ADMIN)
     */
    @PostMapping("/{id}/roles/{role}")
    public ResponseEntity<User> addRole(@PathVariable Long id, @PathVariable User.UserRole role) {
        log.info("➕ POST /api/users/{}/roles/{}", id, role);
        User user = userService.addRole(id, role);
        return ResponseEntity.ok(user);
    }

    /**
     * Retirer un rôle d'un utilisateur (ADMIN)
     */
    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<User> removeRole(@PathVariable Long id, @PathVariable User.UserRole role) {
        log.info("➖ DELETE /api/users/{}/roles/{}", id, role);
        User user = userService.removeRole(id, role);
        return ResponseEntity.ok(user);
    }

    /**
     * Activer un utilisateur (ADMIN)
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        log.info("✅ PUT /api/users/{}/activate", id);
        userService.activateUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Désactiver un utilisateur (ADMIN)
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        log.info("❌ PUT /api/users/{}/deactivate", id);
        userService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User service is running! 👨‍💼");
    }

    /**
     * Extraire le Keycloak ID du JWT
     */
    private String extractKeycloakId(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }
}
