package hr.fer.tinfer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for real-time match notifications sent via SSE.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchNotification {
    private Long matchId;
    private Long conversationId;

    // Info about the matched user (the other person)
    private UUID matchedUserId;
    private String matchedUserName;
    private String matchedUserPhotoUrl;
}
