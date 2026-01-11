package com.ghali.ecommerce.billing.controller;

import com.ghali.ecommerce.billing.model.Invoice;
import com.ghali.ecommerce.billing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des factures
 */
@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        log.info("💰 GET /api/invoices/{}", id);
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<Invoice> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        log.info("💰 GET /api/invoices/number/{}", invoiceNumber);
        Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Invoice> getInvoiceByOrderId(@PathVariable Long orderId) {
        log.info("💰 GET /api/invoices/order/{}", orderId);
        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<Invoice>> getInvoicesByCustomer(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("💰 GET /api/invoices/customer/{}", customerId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Invoice> invoices = invoiceService.getInvoicesByCustomer(customerId, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Invoice>> getOverdueInvoices() {
        log.info("⏰ GET /api/invoices/overdue");
        List<Invoice> invoices = invoiceService.findOverdueInvoices();
        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Invoice> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam Invoice.InvoiceStatus status
    ) {
        log.info("🔄 PUT /api/invoices/{}/status - New status: {}", id, status);
        Invoice invoice = invoiceService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(invoice);
    }

    @PutMapping("/{id}/discount")
    public ResponseEntity<Invoice> applyDiscount(
            @PathVariable Long id,
            @RequestParam BigDecimal amount
    ) {
        log.info("💸 PUT /api/invoices/{}/discount - Amount: {}", id, amount);
        Invoice invoice = invoiceService.applyDiscount(id, amount);
        return ResponseEntity.ok(invoice);
    }

    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("📊 GET /api/invoices/revenue?start={}&end={}", startDate, endDate);
        BigDecimal revenue = invoiceService.getTotalRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Billing service is running! 💰");
    }
}
