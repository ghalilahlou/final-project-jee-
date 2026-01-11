package com.ghali.ecommerce.customer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Customer - Client acheteur
 * Rôle: CUSTOMER dans Keycloak
 */
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_keycloak_id", columnList = "keycloak_id", unique = true),
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_phone", columnList = "phone")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Keycloak ID est obligatoire")
    private String keycloakId;

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

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    // Préférences client
    @Column(name = "newsletter_subscribed")
    @Builder.Default
    private Boolean newsletterSubscribed = false;

    @Column(name = "preferred_language", length = 10)
    @Builder.Default
    private String preferredLanguage = "FR";

    // Adresses
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CustomerAddress> addresses = new ArrayList<>();

    // Statistiques
    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_spent", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "loyalty_points")
    @Builder.Default
    private Integer loyaltyPoints = 0;

    // Status compte
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "verified_email")
    @Builder.Default
    private Boolean verifiedEmail = false;

    @Column(name = "verified_phone")
    @Builder.Default
    private Boolean verifiedPhone = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    public enum Gender {
        MALE, FEMALE, OTHER
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
        return email;
    }

    /**
     * Ajouter une adresse
     */
    public void addAddress(CustomerAddress address) {
        addresses.add(address);
        address.setCustomer(this);
    }

    /**
     * Mettre à jour statistiques après commande
     */
    public void updateOrderStats(BigDecimal orderAmount) {
        this.totalOrders++;
        this.totalSpent = this.totalSpent.add(orderAmount);
        // Points de fidélité: 1 point par 10 MAD
        int pointsEarned = orderAmount.divide(BigDecimal.TEN, RoundingMode.DOWN).intValue();
        this.loyaltyPoints += pointsEarned;
    }
}
