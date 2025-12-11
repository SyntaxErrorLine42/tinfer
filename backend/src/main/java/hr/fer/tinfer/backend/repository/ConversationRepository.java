package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.user.id = :userId AND c.isArchived = false ORDER BY c.lastMessageAt DESC")
    List<Conversation> findActiveConversationsByUserId(UUID userId);
}
