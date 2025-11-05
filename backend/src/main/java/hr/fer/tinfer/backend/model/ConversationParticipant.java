package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "conversation_participants", schema = "public", indexes = {
        @Index(name = "conversation_participants_user_id_idx", columnList = "user_id")
})
public class ConversationParticipant {
    @EmbeddedId
    private ConversationParticipantId id;

    @MapsId("conversationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Profile user;

    @ColumnDefault("now()")
    @Column(name = "joined_at")
    private Instant joinedAt;

    @Column(name = "last_read_at")
    private Instant lastReadAt;

    @ColumnDefault("false")
    @Column(name = "is_muted")
    private Boolean isMuted;

}