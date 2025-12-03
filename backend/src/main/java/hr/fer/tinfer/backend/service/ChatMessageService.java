package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.ChatMessageRequest;
import hr.fer.tinfer.backend.dto.MessageResponse;
import hr.fer.tinfer.backend.model.Conversation;
import hr.fer.tinfer.backend.model.Message;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.MessageRepository;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ConversationPermissionService conversationPermissionService;
    private final ProfileRepository profileRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageResponse handleIncomingMessage(Long conversationId, UUID senderId, ChatMessageRequest request) {
        Conversation conversation = conversationPermissionService.requireParticipant(conversationId, senderId);

        Profile sender = profileRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender profile not found"));

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(request.getContent());
        message.setAttachmentUrl(request.getAttachmentUrl());
        message.setSentAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        conversation.setLastMessageAt(saved.getSentAt());
        updateReadState(conversation, senderId, saved.getSentAt());

        MessageResponse response = toResponse(saved);
        messagingTemplate.convertAndSend(String.format("/topic/chat/%d", conversationId), response);

        log.debug("Broadcasted message {} in conversation {}", saved.getId(), conversationId);
        return response;
    }

    private void updateReadState(Conversation conversation, UUID senderId, LocalDateTime sentAt) {
        conversation.getParticipants().stream()
                .filter(participant -> Objects.equals(participant.getUser().getId(), senderId))
                .findFirst()
                .ifPresent(participant -> participant.setLastReadAt(sentAt));
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
