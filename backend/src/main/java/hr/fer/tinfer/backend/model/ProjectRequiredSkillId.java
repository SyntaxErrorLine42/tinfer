package hr.fer.tinfer.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class ProjectRequiredSkillId implements Serializable {
    private static final long serialVersionUID = 3246666950887579734L;
    @Column(name = "project_id", nullable = false)
    private Integer projectId;

    @Column(name = "skill_id", nullable = false)
    private Integer skillId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProjectRequiredSkillId entity = (ProjectRequiredSkillId) o;
        return Objects.equals(this.skillId, entity.skillId) &&
                Objects.equals(this.projectId, entity.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skillId, projectId);
    }

}