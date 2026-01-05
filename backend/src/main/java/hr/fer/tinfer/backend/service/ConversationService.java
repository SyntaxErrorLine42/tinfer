package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.ConversationSummaryResponse;
import hr.fer.tinfer.backend.model.Conversation;
import hr.fer.tinfer.backend.model.Photo;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.ConversationRepository;
import hr.fer.tinfer.backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public List<ConversationSummaryResponse> getConversationsForUser(UUID userId) {
        List<Conversation> conversations = conversationRepository.findActiveConversationsByUserId(userId);

        return conversations.stream()
                .map(conversation -> toSummary(conversation, userId))
                .sorted(Comparator.comparing(ConversationSummaryResponse::getLastMessageAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private ConversationSummaryResponse toSummary(Conversation conversation, UUID currentUserId) {
        Profile partner = conversation.getParticipants().stream()
                .map(participant -> participant.getUser())
                .filter(profile -> !profile.getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        String partnerPhoto = resolvePrimaryPhoto(partner);
        String partnerName = partner != null ? displayName(partner) : "Unknown";

        String lastMessageSnippet = messageRepository.findTopByConversation_IdOrderBySentAtDesc(conversation.getId())
                .map(message -> truncate(message.getContent(), 120))
                .orElse(null);

        long unread = messageRepository.countUnreadMessages(conversation.getId(), currentUserId);

        return ConversationSummaryResponse.builder()
                .conversationId(conversation.getId())
                .partnerId(partner != null ? partner.getId() : null)
                .partnerDisplayName(partnerName)
                .partnerPrimaryPhotoBase64(partnerPhoto)
                .lastMessageSnippet(lastMessageSnippet)
                .lastMessageAt(conversation.getLastMessageAt())
                .unreadCount(unread)
                .build();
    }

    private String displayName(Profile profile) {
        if (profile == null) {
            return "Unknown";
        }
        if (profile.getDisplayName() != null && !profile.getDisplayName().isBlank()) {
            return profile.getDisplayName();
        }
        return profile.getFirstName();
    }

    private String resolvePrimaryPhoto(Profile profile) {
        if (profile == null || profile.getPhotos() == null) {
            return null;
        }
        Optional<Photo> primary = profile.getPhotos().stream()
                .sorted(Comparator
                        .comparing((Photo photo) -> Boolean.TRUE.equals(photo.getIsPrimary())).reversed()
                        .thenComparing(Photo::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Photo::getId, Comparator.nullsLast(Long::compareTo)))
                .findFirst();
        return primary.map(Photo::getBase64Data).orElse(null);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength) + "…";
    }
}
