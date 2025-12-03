package hr.fer.tinfer.backend.dto;

import hr.fer.tinfer.backend.types.SwipeAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwipeResponse {

    private UUID swipedUserId;
    private SwipeAction action;

    private boolean matchCreated;
    private Long matchId;
    private Long conversationId;
}
