package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "user_skills", indexes = {
        @Index(name = "user_skills_user_id_skill_id_idx", columnList = "user_id, skill_id", unique = true),
        @Index(name = "user_skills_user_id_idx", columnList = "user_id"),
        @Index(name = "user_skills_skill_id_idx", columnList = "skill_id")
})
public class UserSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "proficiency_level")
    private Integer proficiencyLevel;

    @Column(name = "years_of_experience", precision = 3, scale = 1)
    private BigDecimal yearsOfExperience;
}