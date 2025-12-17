package hr.fer.tinfer.backend.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class ConversationSummaryResponse {
    Long conversationId;
    UUID partnerId;
    String partnerDisplayName;
    String partnerPrimaryPhotoBase64; // Base64 encoded image
    String lastMessageSnippet;
    LocalDateTime lastMessageAt;
    long unreadCount;
}
