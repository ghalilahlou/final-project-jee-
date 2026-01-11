package com.ghali.ecommerce.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Product - Catalogue produits
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_sku", columnList = "sku", unique = true),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "SKU est obligatoire")
    private String sku;

    @Column(nullable = false, length = 200)
    @NotBlank(message = "Le nom du produit est obligatoire")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stockQuantity;

    @Column(length = 100)
    private String category;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private Double rating;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
     * Vérifie si le produit est en stock
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Diminue le stock du produit
     */
    public void decreaseStock(int quantity) {
        if (stockQuantity < quantity) {
            throw new IllegalStateException("Stock insuffisant pour le produit: " + sku);
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Augmente le stock du produit
     */
    public void increaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
