package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.MarkMessagesReadRequest;
import hr.fer.tinfer.backend.dto.MessageResponse;
import hr.fer.tinfer.backend.model.Conversation;
import hr.fer.tinfer.backend.model.Message;
import hr.fer.tinfer.backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryService {

    private final ConversationPermissionService conversationPermissionService;
    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public Page<MessageResponse> getMessages(Long conversationId, UUID userId, Pageable pageable) {
        conversationPermissionService.requireParticipant(conversationId, userId);
        return messageRepository.findByConversation_Id(conversationId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public void markAsRead(Long conversationId, UUID userId, MarkMessagesReadRequest request) {
        Conversation conversation = conversationPermissionService.requireParticipant(conversationId, userId);
        LocalDateTime readAt = LocalDateTime.now();

        List<Message> messagesToUpdate = messageRepository.findUnreadMessagesUpTo(conversationId, userId,
                request.getLastReadMessageId());
        messagesToUpdate.forEach(message -> {
            message.setIsRead(true);
            message.setReadAt(readAt);
        });

        if (!messagesToUpdate.isEmpty()) {
            messageRepository.saveAll(messagesToUpdate);
            log.debug("Marked {} messages as read in conversation {} for user {}", messagesToUpdate.size(),
                    conversationId, userId);
        }

        conversation.getParticipants().stream()
                .filter(participant -> participant.getUser().getId().equals(userId))
                .findFirst()
                .ifPresent(participant -> participant.setLastReadAt(readAt));
    }

    private MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .attachmentUrl(message.getAttachmentUrl())
                .sentAt(message.getSentAt())
                .read(Boolean.TRUE.equals(message.getIsRead()))
                .readAt(message.getReadAt())
                .build();
    }
}
