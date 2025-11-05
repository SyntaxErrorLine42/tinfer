package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "project_preferred_skills", schema = "public", indexes = {
        @Index(name = "project_preferred_skills_skill_id_idx", columnList = "skill_id")
})
public class ProjectPreferredSkill {
    @EmbeddedId
    private ProjectPreferredSkillId id;

    @MapsId("projectId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectPost project;

    @MapsId("skillId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

}