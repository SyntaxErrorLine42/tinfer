package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "project_profiles", indexes = {
        @Index(name = "project_profiles_user_id_idx", columnList = "user_id", unique = true),
        @Index(name = "project_profiles_is_looking_idx", columnList = "is_looking")
})
public class ProjectProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @Column(name = "linkedin_url", length = 500)
    private String linkedinUrl;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    @Column(name = "hours_per_week")
    private Integer hoursPerWeek;

    @ColumnDefault("true")
    @Column(name = "is_looking")
    private Boolean isLooking;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;
}