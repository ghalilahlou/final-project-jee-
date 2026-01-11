package com.ghali.ecommerce.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Service de gestion des clients acheteurs
 * Distinct du User Service (admin/vendeurs)
 */
@SpringBootApplication
@EnableCaching
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
