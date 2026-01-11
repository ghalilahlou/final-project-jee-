package com.ghali.ecommerce.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entité Invoice - Facture
 */
@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_invoice_number", columnList = "invoice_number", unique = true),
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le numéro de facture est obligatoire")
    private String invoiceNumber;

    @Column(name = "order_id", nullable = false)
    @NotNull(message = "L'ID de commande est obligatoire")
    private Long orderId;

    @Column(name = "order_number", length = 50)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    @NotBlank(message = "L'ID client est obligatoire")
    private String customerId;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "customer_email", length = 200)
    private String customerEmail;

    @Column(name = "customer_address", columnDefinition = "TEXT")
    private String customerAddress;

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("0.20"); // 20% TVA

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public enum InvoiceStatus {
        DRAFT,          // Brouillon
        ISSUED,         // Émise
        SENT,           // Envoyée au client
        PAID,           // Payée
        OVERDUE,        // En retard
        CANCELLED,      // Annulée
        REFUNDED        // Remboursée
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (invoiceDate == null) {
            invoiceDate = LocalDate.now();
        }
        if (dueDate == null) {
            dueDate = invoiceDate.plusDays(30); // 30 jours par défaut
        }
        calculateAmounts();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateAmounts();
    }

    /**
     * Calcule les montants (TVA et total)
     */
    public void calculateAmounts() {
        if (subtotal == null) {
            subtotal = BigDecimal.ZERO;
        }
        if (discountAmount == null) {
            discountAmount = BigDecimal.ZERO;
        }
        if (taxRate == null) {
            taxRate = new BigDecimal("0.20");
        }

        // Calcul : Subtotal - Discount
        BigDecimal amountAfterDiscount = subtotal.subtract(discountAmount);
        
        // Calcul TVA
        taxAmount = amountAfterDiscount.multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);
        
        // Calcul Total
        totalAmount = amountAfterDiscount.add(taxAmount)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Marquer la facture comme payée
     */
    public void markAsPaid() {
        this.status = InvoiceStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    /**
     * Vérifier si la facture est en retard
     */
    public boolean isOverdue() {
        return status != InvoiceStatus.PAID 
                && status != InvoiceStatus.CANCELLED 
                && dueDate != null 
                && LocalDate.now().isAfter(dueDate);
    }
}
