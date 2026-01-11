package com.ghali.ecommerce.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Service de gestion des utilisateurs système
 * Rôles: ADMIN, VENDOR (vendeurs agré gés)
 * Distinct du Customer Service (acheteurs)
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
