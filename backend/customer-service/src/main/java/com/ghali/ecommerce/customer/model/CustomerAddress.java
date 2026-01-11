package com.ghali.ecommerce.customer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité CustomerAddress - Adresse client
 */
@Entity
@Table(name = "customer_addresses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "label", length = 50)
    private String label; // "Domicile", "Bureau", etc.

    @Column(name = "full_name", length = 200)
    @NotBlank(message = "Le nom complet est obligatoire")
    private String fullName;

    @Column(name = "address_line1", nullable = false)
    @NotBlank(message = "L'adresse est obligatoire")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La ville est obligatoire")
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Le pays est obligatoire")
    @Builder.Default
    private String country = "Maroc";

    @Column(length = 20)
    private String phone;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * Obtenir l'adresse formatée
     */
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(addressLine1);
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            sb.append(", ").append(addressLine2);
        }
        sb.append(", ").append(city);
        if (zipCode != null) {
            sb.append(" ").append(zipCode);
        }
        if (state != null) {
            sb.append(", ").append(state);
        }
        sb.append(", ").append(country);
        return sb.toString();
    }
}
