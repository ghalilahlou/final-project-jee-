package com.ghali.ecommerce.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Service de Facturation et Paiements
 * Gère la génération automatique de factures via Kafka
 */
@SpringBootApplication
@EnableKafka
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }
}
