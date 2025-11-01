package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import hr.fer.tinfer.backend.types.match_type;

@Getter
@Setter
@Entity
@Table(name = "conversations", indexes = {
        @Index(name = "conversations_match_id_idx", columnList = "match_id"),
        @Index(name = "conversations_last_message_at_idx", columnList = "last_message_at")
})
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @ColumnDefault("false")
    @Column(name = "is_archived")
    private Boolean isArchived;

    @OneToMany(mappedBy = "conversation")
    private Set<ConversationParticipant> conversationParticipants = new LinkedHashSet<>();
    @OneToMany(mappedBy = "conversation")
    private Set<Message> messages = new LinkedHashSet<>();

    @Column(name = "conversation_type", columnDefinition = "match_type not null")
    private match_type conversationType;
}