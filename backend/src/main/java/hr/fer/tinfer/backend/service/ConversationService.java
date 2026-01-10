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

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // Single query to get all conversations with participants and photos
        // pre-fetched
        List<Conversation> conversations = conversationRepository
                .findActiveConversationsWithParticipantsAndPhotos(userId);

        if (conversations.isEmpty()) {
            return List.of();
        }

        // Get all conversation IDs
        List<Long> conversationIds = conversations.stream()
                .map(Conversation::getId)
                .collect(Collectors.toList());

        // Batch fetch last messages for all conversations (single query)
        Map<Long, String> lastMessages = new HashMap<>();
        messageRepository.findLastMessagesForConversations(conversationIds).forEach(row -> {
            Long convId = (Long) row[0];
            String content = (String) row[1];
            lastMessages.put(convId, truncate(content, 120));
        });

        // Batch fetch unread counts for all conversations (single query)
        Map<Long, Long> unreadCounts = new HashMap<>();
        messageRepository.countUnreadMessagesForConversations(conversationIds, userId).forEach(row -> {
            Long convId = (Long) row[0];
            Long count = (Long) row[1];
            unreadCounts.put(convId, count);
        });

        // Build responses (no additional queries needed)
        return conversations.stream()
                .map(conversation -> toSummary(conversation, userId, lastMessages, unreadCounts))
                .sorted(Comparator.comparing(ConversationSummaryResponse::getLastMessageAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private ConversationSummaryResponse toSummary(
            Conversation conversation,
            UUID currentUserId,
            Map<Long, String> lastMessages,
            Map<Long, Long> unreadCounts) {

        Profile partner = conversation.getParticipants().stream()
                .map(participant -> participant.getUser())
                .filter(profile -> !profile.getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        String partnerPhoto = resolvePrimaryPhotoUrl(partner);
        String partnerName = partner != null ? displayName(partner) : "Unknown";

        return ConversationSummaryResponse.builder()
                .conversationId(conversation.getId())
                .partnerId(partner != null ? partner.getId() : null)
                .partnerDisplayName(partnerName)
                .partnerPhotoUrl(partnerPhoto)
                .lastMessageSnippet(lastMessages.getOrDefault(conversation.getId(), null))
                .lastMessageAt(conversation.getLastMessageAt())
                .unreadCount(unreadCounts.getOrDefault(conversation.getId(), 0L))
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

    /**
     * Get the URL of the primary photo for a profile.
     */
    private String resolvePrimaryPhotoUrl(Profile profile) {
        if (profile == null || profile.getPhotos() == null) {
            return null;
        }
        Optional<Photo> primary = profile.getPhotos().stream()
                .sorted(Comparator
                        .comparing((Photo photo) -> Boolean.TRUE.equals(photo.getIsPrimary())).reversed()
                        .thenComparing(Photo::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Photo::getId, Comparator.nullsLast(Long::compareTo)))
                .findFirst();
        return primary.map(Photo::getStorageUrl).orElse(null);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength) + "…";
    }
}
