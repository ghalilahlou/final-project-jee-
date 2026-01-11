package com.ghali.ecommerce.billing.repository;

import com.ghali.ecommerce.billing.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByOrderId(Long orderId);

    List<Invoice> findByCustomerId(String customerId);

    Page<Invoice> findByCustomerId(String customerId, Pageable pageable);

    List<Invoice> findByStatus(Invoice.InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.status = :status AND i.dueDate < :date")
    List<Invoice> findOverdueInvoices(@Param("status") Invoice.InvoiceStatus status,
            @Param("date") LocalDate date);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByInvoiceDateBetween(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.invoiceDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueForPeriod(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.customerId = :customerId AND i.status = 'PAID'")
    long countPaidInvoicesByCustomer(@Param("customerId") String customerId);
}
