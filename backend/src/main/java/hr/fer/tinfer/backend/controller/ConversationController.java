package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.dto.ChatMessageRequest;
import hr.fer.tinfer.backend.dto.ConversationSummaryResponse;
import hr.fer.tinfer.backend.dto.MarkMessagesReadRequest;
import hr.fer.tinfer.backend.dto.MessageResponse;
import hr.fer.tinfer.backend.service.ChatHistoryService;
import hr.fer.tinfer.backend.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversations", description = "List of conversations and message history")
@SecurityRequirement(name = "Bearer Authentication")
public class ConversationController {

    private final ConversationService conversationService;
    private final ChatHistoryService chatHistoryService;

    @GetMapping
    @Operation(summary = "Get all conversations of the current user")
    public ResponseEntity<List<ConversationSummaryResponse>> getMyConversations(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<ConversationSummaryResponse> response = conversationService.getConversationsForUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conversationId}/messages")
    @Operation(summary = "Get messages in a conversation with pagination")
    public ResponseEntity<Page<MessageResponse>> getMessages(@PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(size, 100),
                Sort.by(Sort.Direction.ASC, "sentAt"));
        Page<MessageResponse> messages = chatHistoryService.getMessages(conversationId, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{conversationId}/read")
    @Operation(summary = "Mark messages as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long conversationId,
            @Valid @RequestBody MarkMessagesReadRequest request,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        chatHistoryService.markAsRead(conversationId, userId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{conversationId}/messages")
    @Operation(summary = "Send a message to a conversation")
    public ResponseEntity<MessageResponse> sendMessage(@PathVariable Long conversationId,
            @Valid @RequestBody ChatMessageRequest request,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        MessageResponse message = chatHistoryService.sendMessage(conversationId, userId, request);
        return ResponseEntity.ok(message);
    }
}
