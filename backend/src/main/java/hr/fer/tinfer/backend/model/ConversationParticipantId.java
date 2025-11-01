package hr.fer.tinfer.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class ConversationParticipantId implements Serializable {
    private static final long serialVersionUID = 6944715385671014006L;
    @Column(name = "conversation_id", nullable = false)
    private Integer conversationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        ConversationParticipantId entity = (ConversationParticipantId) o;
        return Objects.equals(this.conversationId, entity.conversationId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, userId);
    }

}