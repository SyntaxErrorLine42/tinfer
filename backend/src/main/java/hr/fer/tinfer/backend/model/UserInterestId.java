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
public class UserInterestId implements Serializable {
    private static final long serialVersionUID = 8507884207524857098L;
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "interest_id", nullable = false)
    private Integer interestId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        UserInterestId entity = (UserInterestId) o;
        return Objects.equals(this.interestId, entity.interestId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interestId, userId);
    }

}