package com.ghali.ecommerce.chatbot.controller;

import com.ghali.ecommerce.chatbot.dto.ChatMessageDTO;
import com.ghali.ecommerce.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API REST pour le chatbot
 */
@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody ChatMessageDTO messageDTO) {
        log.info("📨 Received chat message from user: {}", messageDTO.getUserId());
        
        ChatMessageDTO response = chatbotService.processMessage(messageDTO);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chatbot service is running! 🤖");
    }
}
