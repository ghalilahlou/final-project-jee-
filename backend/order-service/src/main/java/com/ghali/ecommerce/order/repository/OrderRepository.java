package com.ghali.ecommerce.order.repository;

import com.ghali.ecommerce.common.dto.OrderDTO;
import com.ghali.ecommerce.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerId(String customerId);

    Page<Order> findByCustomerId(String customerId, Pageable pageable);

    List<Order> findByStatus(OrderDTO.OrderStatus status);

    Page<Order> findByStatus(OrderDTO.OrderStatus status, Pageable pageable);
}
