package com.ghali.ecommerce.chatbot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant une conversation de chat
 */
@Entity
@Table(name = "chat_conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String conversationId;

    @Column(nullable = false)
    private String userId;

    @Column(name = "user_message", columnDefinition = "TEXT")
    private String userMessage;

    @Column(name = "bot_response", columnDefinition = "TEXT")
    private String botResponse;

    @Enumerated(EnumType.STRING)
    private MessageIntent intent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_resolved")
    private Boolean isResolved;

    public enum MessageIntent {
        PRODUCT_SEARCH,
        ORDER_TRACKING,
        CUSTOMER_SUPPORT,
        FAQ,
        GENERAL_INQUIRY,
        UNKNOWN
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isResolved == null) {
            isResolved = false;
        }
    }
}
