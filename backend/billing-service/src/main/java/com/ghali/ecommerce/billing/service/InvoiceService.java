package com.ghali.ecommerce.billing.service;

import com.ghali.ecommerce.billing.kafka.BillingEventProducer;
import com.ghali.ecommerce.billing.model.Invoice;
import com.ghali.ecommerce.billing.repository.InvoiceRepository;
import com.ghali.ecommerce.common.dto.OrderDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Service de gestion des factures
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BillingEventProducer billingEventProducer;

    @Value("${billing.invoice.prefix:INV}")
    private String invoicePrefix;

    @Value("${billing.tax.vat-rate:0.20}")
    private BigDecimal defaultTaxRate;

    /**
     * Créer une facture à partir d'une commande
     */
    @Transactional
    public Invoice createInvoiceFromOrder(OrderDTO order) {
        log.info("💰 Creating invoice for order: {}", order.getOrderNumber());

        // Vérifier si une facture existe déjà
        if (invoiceRepository.findByOrderId(order.getId()).isPresent()) {
            throw new RuntimeException("Invoice already exists for order: " + order.getOrderNumber());
        }

        // Générer le numéro de facture
        String invoiceNumber = generateInvoiceNumber();

        // Construire l'adresse client
        String customerAddress = buildCustomerAddress(order);

        // Créer la facture
        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerName(order.getShippingAddress() != null 
                        ? order.getShippingAddress().getFullName() 
                        : "Client")
                .customerEmail(order.getCustomerEmail())
                .customerAddress(customerAddress)
                .subtotal(order.getTotalAmount())
                .taxRate(defaultTaxRate)
                .discountAmount(BigDecimal.ZERO)
                .status(Invoice.InvoiceStatus.ISSUED)
                .notes("Facture générée automatiquement pour la commande " + order.getOrderNumber())
                .build();

        invoice.calculateAmounts();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Publier événement Kafka
        billingEventProducer.publishInvoiceEvent("INVOICE_CREATED", savedInvoice);

        log.info("✅ Invoice created: {} for order: {}", invoiceNumber, order.getOrderNumber());
        return savedInvoice;
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + id));
    }

    public Invoice getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found with number: " + invoiceNumber));
    }

    public Invoice getInvoiceByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Invoice not found for order ID: " + orderId));
    }

    public List<Invoice> getInvoicesByCustomer(String customerId) {
        return invoiceRepository.findByCustomerId(customerId);
    }

    public Page<Invoice> getInvoicesByCustomer(String customerId, Pageable pageable) {
        return invoiceRepository.findByCustomerId(customerId, pageable);
    }

    public List<Invoice> findOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices(
                Invoice.InvoiceStatus.ISSUED, 
                LocalDate.now()
        );
    }

    @Transactional
    public Invoice updateInvoiceStatus(Long id, Invoice.InvoiceStatus newStatus) {
        log.info("🔄 Updating invoice {} to status: {}", id, newStatus);

        Invoice invoice = getInvoiceById(id);
        Invoice.InvoiceStatus oldStatus = invoice.getStatus();
        invoice.setStatus(newStatus);

        if (newStatus == Invoice.InvoiceStatus.PAID) {
            invoice.markAsPaid();
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        // Publier événement
        billingEventProducer.publishInvoiceEvent("INVOICE_STATUS_UPDATED", updatedInvoice);

        log.info("✅ Invoice {} status updated: {} -> {}", 
                invoice.getInvoiceNumber(), oldStatus, newStatus);

        return updatedInvoice;
    }

    @Transactional
    public Invoice applyDiscount(Long id, BigDecimal discountAmount) {
        log.info("💸 Applying discount {} to invoice {}", discountAmount, id);

        Invoice invoice = getInvoiceById(id);

        if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
            throw new RuntimeException("Cannot apply discount to paid invoice");
        }

        invoice.setDiscountAmount(discountAmount);
        invoice.calculateAmounts();

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        log.info("✅ Discount applied to invoice: {}", invoice.getInvoiceNumber());
        return updatedInvoice;
    }

    /**
     * Générer un numéro de facture unique
     */
    private String generateInvoiceNumber() {
        String year = String.valueOf(Year.now().getValue());
        long count = invoiceRepository.count() + 1;
        return String.format("%s-%s-%06d", invoicePrefix, year, count);
    }

    /**
     * Construire l'adresse du client
     */
    private String buildCustomerAddress(OrderDTO order) {
        if (order.getShippingAddress() == null) {
            return "";
        }

        var addr = order.getShippingAddress();
        return String.format("%s\n%s %s\n%s, %s %s\n%s",
                addr.getFullName(),
                addr.getAddressLine1(),
                addr.getAddressLine2() != null ? addr.getAddressLine2() : "",
                addr.getCity(),
                addr.getState() != null ? addr.getState() : "",
                addr.getZipCode(),
                addr.getCountry()
        ).replaceAll("\n+", "\n").trim();
    }

    /**
     * Calculer le revenu total pour une période
     */
    public BigDecimal getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        BigDecimal revenue = invoiceRepository.getTotalRevenueForPeriod(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}
