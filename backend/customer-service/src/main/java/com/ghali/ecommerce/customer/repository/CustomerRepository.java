package com.ghali.ecommerce.customer.repository;

import com.ghali.ecommerce.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByKeycloakId(String keycloakId);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPhone(String phone);

    List<Customer> findByActiveTrue();

    List<Customer> findByNewsletterSubscribedTrue();

    @Query("SELECT c FROM Customer c WHERE c.totalOrders >= :minOrders ORDER BY c.totalSpent DESC")
    List<Customer> findTopCustomers(@Param("minOrders") int minOrders);

    @Query("SELECT c FROM Customer c WHERE c.loyaltyPoints >= :minPoints")
    List<Customer> findByLoyaltyPointsGreaterThanEqual(@Param("minPoints") int minPoints);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Customer> searchCustomers(@Param("keyword") String keyword);
}
