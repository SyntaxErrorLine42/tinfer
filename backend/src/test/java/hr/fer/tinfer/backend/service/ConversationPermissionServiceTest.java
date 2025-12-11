package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.model.Conversation;
import hr.fer.tinfer.backend.model.ConversationParticipant;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.ConversationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationPermissionServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    private ConversationPermissionService conversationPermissionService;

    @BeforeEach
    void setUp() {
        conversationPermissionService = new ConversationPermissionService(conversationRepository);
    }

    @Test
    void returnsConversationWhenUserIsParticipant() {
        UUID userId = UUID.randomUUID();
        Conversation conversation = buildConversationWithUser(userId);

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        Conversation result = conversationPermissionService.requireParticipant(1L, userId);

        assertEquals(conversation, result);
    }

    @Test
    void throwsSecurityExceptionWhenUserNotParticipant() {
        UUID userId = UUID.randomUUID();
        Conversation conversation = buildConversationWithUser(UUID.randomUUID());

        when(conversationRepository.findById(2L)).thenReturn(Optional.of(conversation));

        assertThrows(SecurityException.class, () -> conversationPermissionService.requireParticipant(2L, userId));
    }

    private Conversation buildConversationWithUser(UUID userId) {
        Conversation conversation = new Conversation();
        Profile profile = new Profile();
        profile.setId(userId);

        ConversationParticipant participant = new ConversationParticipant();
        participant.setConversation(conversation);
        participant.setUser(profile);

        conversation.setParticipants(Set.of(participant));
        return conversation;
    }
}
