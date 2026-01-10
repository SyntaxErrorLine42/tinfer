package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.user.id = :userId AND c.isArchived = false ORDER BY c.lastMessageAt DESC")
    List<Conversation> findActiveConversationsByUserId(UUID userId);

    /**
     * Find conversations with participants and their photos pre-fetched to avoid
     * N+1.
     */
    @Query("""
            SELECT DISTINCT c FROM Conversation c
            LEFT JOIN FETCH c.participants p
            LEFT JOIN FETCH p.user u
            LEFT JOIN FETCH u.photos
            WHERE c.id IN (
                SELECT c2.id FROM Conversation c2
                JOIN c2.participants p2
                WHERE p2.user.id = :userId AND c2.isArchived = false
            )
            ORDER BY c.lastMessageAt DESC
            """)
    List<Conversation> findActiveConversationsWithParticipantsAndPhotos(@Param("userId") UUID userId);
}
