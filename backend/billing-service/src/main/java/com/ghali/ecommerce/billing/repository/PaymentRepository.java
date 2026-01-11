package com.ghali.ecommerce.billing.repository;

import com.ghali.ecommerce.billing.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByInvoiceId(Long invoiceId);

    List<Payment> findByStatus(Payment.PaymentStatus status);

    Optional<Payment> findByProviderTransactionId(String providerTransactionId);
}
