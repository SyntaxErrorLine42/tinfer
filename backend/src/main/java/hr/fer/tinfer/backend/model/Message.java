package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_messages_conversation", columnList = "conversation_id, sent_at"),
        @Index(name = "messages_sender_id_idx", columnList = "sender_id")
})
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Profile sender;

    @Column(name = "content", nullable = false, length = Integer.MAX_VALUE)
    private String content;

    @ColumnDefault("now()")
    @Column(name = "sent_at")
    private Instant sentAt;

    @ColumnDefault("false")
    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;
}