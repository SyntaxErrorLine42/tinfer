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
public class UserDepartmentId implements Serializable {
    private static final long serialVersionUID = 7321181794402346897L;
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        UserDepartmentId entity = (UserDepartmentId) o;
        return Objects.equals(this.departmentId, entity.departmentId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departmentId, userId);
    }

}