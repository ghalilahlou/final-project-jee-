package com.ghali.ecommerce.chatbot.dto;

import com.ghali.ecommerce.chatbot.model.ChatConversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour les messages du chatbot
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private String conversationId;
    private String userId;
    private String message;
    private String response;
    private ChatConversation.MessageIntent intent;
    private LocalDateTime timestamp;
    private Boolean isFromBot;
}
