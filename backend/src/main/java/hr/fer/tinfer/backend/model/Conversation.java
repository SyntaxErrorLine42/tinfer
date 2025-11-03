package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import hr.fer.tinfer.backend.types.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "conversations", schema = "public", indexes = {
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

    @Enumerated(EnumType.STRING)
    @Column(name = "conversation_type", nullable = false)
    private match_type conversationType;

}