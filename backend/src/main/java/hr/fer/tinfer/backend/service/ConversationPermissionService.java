package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.model.Conversation;
import hr.fer.tinfer.backend.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationPermissionService {

    private final ConversationRepository conversationRepository;

    @Transactional(readOnly = true)
    public Conversation requireParticipant(Long conversationId, UUID userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        boolean participant = conversation.getParticipants().stream()
                .anyMatch(p -> Objects.equals(p.getUser().getId(), userId));

        if (!participant) {
            log.warn("User {} attempted to access conversation {} without membership", userId, conversationId);
            throw new SecurityException("You are not a member of this conversation");
        }

        return conversation;
    }
}
