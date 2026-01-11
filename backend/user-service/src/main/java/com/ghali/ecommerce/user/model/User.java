package com.ghali.ecommerce.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité User - Utilisateur système
 * Rôles: ADMIN, VENDOR
 * Distinct des Customers (acheteurs)
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_keycloak_id", columnList = "keycloak_id", unique = true),
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_username", columnList = "username", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Keycloak ID est obligatoire")
    private String keycloakId;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Username est obligatoire")
    private String username;

    @Column(unique = true, nullable = false, length = 200)
    @Email(message = "Email invalide")
    @NotBlank(message = "Email est obligatoire")
    private String email;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    // Rôles - géré aussi dans Keycloak
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    // Informations spécifiques aux vendeurs
    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "company_address", columnDefinition = "TEXT")
    private String companyAddress;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate; // Taux de commission pour vendeurs

    // Status compte
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "verified_email")
    @Builder.Default
    private Boolean verifiedEmail = false;

    // Métadonnées
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    public enum UserRole {
        ADMIN,      // Administrateur système
        VENDOR      // Vendeur agrégé
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Obtenir le nom complet
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username;
    }

    /**
     * Vérifier si l'utilisateur est admin
     */
    public boolean isAdmin() {
        return roles.contains(UserRole.ADMIN);
    }

    /**
     * Vérifier si l'utilisateur est vendeur
     */
    public boolean isVendor() {
        return roles.contains(UserRole.VENDOR);
    }

    /**
     * Ajouter un rôle
     */
    public void addRole(UserRole role) {
        this.roles.add(role);
    }

    /**
     * Retirer un rôle
     */
    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }
}
