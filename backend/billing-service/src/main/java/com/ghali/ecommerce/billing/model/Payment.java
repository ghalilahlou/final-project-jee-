package com.ghali.ecommerce.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité Payment - Paiement
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_transaction_id", columnList = "transaction_id", unique = true),
    @Index(name = "idx_invoice_id", columnList = "invoice_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false, length = 100)
    @NotBlank(message = "L'ID de transaction est obligatoire")
    private String transactionId;

    @Column(name = "invoice_id", nullable = false)
    @NotNull(message = "L'ID de facture est obligatoire")
    private Long invoiceId;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private PaymentMethod method = PaymentMethod.CREDIT_CARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_provider", length = 100)
    private String paymentProvider; // Stripe, PayPal, etc.

    @Column(name = "provider_transaction_id", length = 200)
    private String providerTransactionId;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "card_brand", length = 50)
    private String cardBrand; // Visa, Mastercard, etc.

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL,
        BANK_TRANSFER,
        CASH,
        MOBILE_PAYMENT,
        CRYPTOCURRENCY
    }

    public enum PaymentStatus {
        PENDING,        // En attente
        PROCESSING,     // En cours de traitement
        COMPLETED,      // Complété
        FAILED,         // Échoué
        REFUNDED,       // Remboursé
        CANCELLED       // Annulé
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
     * Marquer le paiement comme complété
     */
    public void markAsCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.paymentDate = LocalDateTime.now();
    }
}
