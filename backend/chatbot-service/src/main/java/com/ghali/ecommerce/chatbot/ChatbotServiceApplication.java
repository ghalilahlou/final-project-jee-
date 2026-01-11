package com.ghali.ecommerce.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Service de Chatbot Intelligent
 * Inspiré du projet chatbot original avec intégration Kafka
 */
@SpringBootApplication
@EnableKafka
public class ChatbotServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotServiceApplication.class, args);
    }
}
