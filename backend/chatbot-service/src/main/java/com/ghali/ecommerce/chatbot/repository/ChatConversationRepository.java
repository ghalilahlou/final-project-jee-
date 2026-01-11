package com.ghali.ecommerce.chatbot.repository;

import com.ghali.ecommerce.chatbot.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    
    List<ChatConversation> findByUserId(String userId);
    
    List<ChatConversation> findByConversationId(String conversationId);
    
    List<ChatConversation> findByIsResolvedFalse();
}
