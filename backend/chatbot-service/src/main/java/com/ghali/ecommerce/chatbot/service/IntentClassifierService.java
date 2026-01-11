package com.ghali.ecommerce.chatbot.service;

import com.ghali.ecommerce.chatbot.model.ChatConversation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service pour classifier l'intention des messages utilisateur
 */
@Service
@Slf4j
public class IntentClassifierService {

    // Patterns pour identifier les intentions
    private static final Map<ChatConversation.MessageIntent, Pattern[]> INTENT_PATTERNS = Map.of(
        ChatConversation.MessageIntent.PRODUCT_SEARCH, new Pattern[]{
            Pattern.compile(".*\\b(cherche|recherche|trouve|acheter|produit|article)\\b.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\b(search|find|looking for|product)\\b.*", Pattern.CASE_INSENSITIVE)
        },
        
        ChatConversation.MessageIntent.ORDER_TRACKING, new Pattern[]{
            Pattern.compile(".*\\b(commande|suivre|tracking|livraison|où est)\\b.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\b(order|track|delivery|shipping|where is)\\b.*", Pattern.CASE_INSENSITIVE)
        },
        
        ChatConversation.MessageIntent.CUSTOMER_SUPPORT, new Pattern[]{
            Pattern.compile(".*\\b(aide|help|assistance|problème|issue|bug)\\b.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\b(support|contact|complaint)\\b.*", Pattern.CASE_INSENSITIVE)
        },
        
        ChatConversation.MessageIntent.FAQ, new Pattern[]{
            Pattern.compile(".*\\b(comment|pourquoi|quand|faq|question)\\b.*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*\\b(how|why|when|what|faq)\\b.*", Pattern.CASE_INSENSITIVE)
        }
    );

    public ChatConversation.MessageIntent classifyIntent(String message) {
        if (message == null || message.trim().isEmpty()) {
            return ChatConversation.MessageIntent.UNKNOWN;
        }

        log.info("🎯 Classifying intent for message: {}", message);

        // Parcourir les patterns et trouver une correspondance
        for (Map.Entry<ChatConversation.MessageIntent, Pattern[]> entry : INTENT_PATTERNS.entrySet()) {
            for (Pattern pattern : entry.getValue()) {
                if (pattern.matcher(message).matches()) {
                    log.info("✅ Intent classified as: {}", entry.getKey());
                    return entry.getKey();
                }
            }
        }

        // Si aucune correspondance, c'est une requête générale
        return ChatConversation.MessageIntent.GENERAL_INQUIRY;
    }
}
