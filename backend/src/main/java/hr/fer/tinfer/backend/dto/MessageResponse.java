package hr.fer.tinfer.backend.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class MessageResponse {
    Long id;
    Long conversationId;
    UUID senderId;
    String content;
    String attachmentUrl;
    Boolean read;
    LocalDateTime sentAt;
    LocalDateTime readAt;
}
