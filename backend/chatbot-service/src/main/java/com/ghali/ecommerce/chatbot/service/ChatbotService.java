package com.ghali.ecommerce.chatbot.service;

import com.ghali.ecommerce.chatbot.dto.ChatMessageDTO;
import com.ghali.ecommerce.chatbot.model.ChatConversation;
import com.ghali.ecommerce.chatbot.repository.ChatConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service principal du chatbot
 * Inspiré du projet chatbot original avec intelligence conversationnelle
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatConversationRepository conversationRepository;
    private final IntentClassifierService intentClassifier;

    @Value("${chatbot.use-ai:false}")
    private boolean useAI;

    public ChatMessageDTO processMessage(ChatMessageDTO messageDTO) {
        log.info("💬 Processing message from user: {}", messageDTO.getUserId());

        // Générer un ID de conversation si nécessaire
        if (messageDTO.getConversationId() == null) {
            messageDTO.setConversationId(UUID.randomUUID().toString());
        }

        // Classifier l'intention du message
        ChatConversation.MessageIntent intent = intentClassifier.classifyIntent(messageDTO.getMessage());
        messageDTO.setIntent(intent);

        // Générer la réponse
        String response = generateResponse(messageDTO.getMessage(), intent);
        messageDTO.setResponse(response);
        messageDTO.setTimestamp(LocalDateTime.now());
        messageDTO.setIsFromBot(true);

        // Sauvegarder la conversation
        saveConversation(messageDTO);

        return messageDTO;
    }

    private String generateResponse(String userMessage, ChatConversation.MessageIntent intent) {
        log.info("🤖 Generating response for intent: {}", intent);

        if (useAI) {
            // TODO: Intégrer avec OpenAI/LangChain4j
            return generateAIResponse(userMessage);
        } else {
            return generatePredefinedResponse(intent, userMessage);
        }
    }

    private String generateAIResponse(String userMessage) {
        // TODO: Implémenter l'appel à l'API OpenAI
        return "Je suis là pour vous aider ! (Mode IA sera activé prochainement)";
    }

    private String generatePredefinedResponse(ChatConversation.MessageIntent intent, String message) {
        return switch (intent) {
            case PRODUCT_SEARCH -> 
                "🔍 Je peux vous aider à trouver des produits ! Que recherchez-vous exactement ?";
            
            case ORDER_TRACKING -> 
                "📦 Pour suivre votre commande, veuillez me fournir votre numéro de commande.";
            
            case CUSTOMER_SUPPORT -> 
                "👋 Je suis là pour vous aider ! Comment puis-je vous assister aujourd'hui ?";
            
            case FAQ -> 
                """
                📚 Voici quelques questions fréquentes :
                - Comment passer une commande ?
                - Quels sont les modes de paiement ?
                - Quel est le délai de livraison ?
                - Comment retourner un produit ?
                """;
            
            case GENERAL_INQUIRY -> 
                "💡 Je suis votre assistant e-commerce. Je peux vous aider avec vos commandes, " +
                "la recherche de produits, ou répondre à vos questions !";
            
            default -> 
                "Je n'ai pas bien compris votre demande. Pouvez-vous reformuler ?";
        };
    }

    private void saveConversation(ChatMessageDTO messageDTO) {
        try {
            ChatConversation conversation = ChatConversation.builder()
                    .conversationId(messageDTO.getConversationId())
                    .userId(messageDTO.getUserId())
                    .userMessage(messageDTO.getMessage())
                    .botResponse(messageDTO.getResponse())
                    .intent(messageDTO.getIntent())
                    .isResolved(false)
                    .build();

            conversationRepository.save(conversation);
            log.info("✅ Conversation saved: {}", conversation.getConversationId());
        } catch (Exception e) {
            log.error("❌ Error saving conversation: {}", e.getMessage());
        }
    }
}
