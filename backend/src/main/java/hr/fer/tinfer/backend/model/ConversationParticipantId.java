package hr.fer.tinfer.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationParticipantId implements Serializable {
    private Long conversation;
    private UUID user;
}
