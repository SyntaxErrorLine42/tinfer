package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.sentAt ASC")
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);

    Page<Message> findByConversation_Id(Long conversationId, Pageable pageable);

    Optional<Message> findTopByConversation_IdOrderBySentAtDesc(Long conversationId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.sender.id <> :userId AND (m.isRead = FALSE OR m.isRead IS NULL)")
    long countUnreadMessages(Long conversationId, UUID userId);

    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.sender.id <> :userId AND m.id <= :lastMessageId AND (m.isRead = FALSE OR m.isRead IS NULL)")
    List<Message> findUnreadMessagesUpTo(Long conversationId, UUID userId, Long lastMessageId);
}
