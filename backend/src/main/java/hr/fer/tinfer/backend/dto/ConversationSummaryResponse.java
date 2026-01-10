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
    String partnerPhotoUrl; // URL to partner's photo in Supabase Storage
    String lastMessageSnippet;
    LocalDateTime lastMessageAt;
    long unreadCount;
}
