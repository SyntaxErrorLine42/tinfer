package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.dto.ChatMessageRequest;
import hr.fer.tinfer.backend.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat/{conversationId}")
    public void processChatMessage(@DestinationVariable Long conversationId,
            @Valid @Payload ChatMessageRequest messageRequest,
            Principal principal) {
        if (principal == null) {
            throw new SecurityException("Missing authenticated principal for WebSocket message");
        }

        UUID senderId = UUID.fromString(principal.getName());
        chatMessageService.handleIncomingMessage(conversationId, senderId, messageRequest);
        log.debug("Processed WebSocket message for conversation {}", conversationId);
    }
}
