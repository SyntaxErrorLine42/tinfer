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
@Table(name = "project_applications", schema = "public", indexes = {
        @Index(name = "project_applications_project_id_applicant_id_idx", columnList = "project_id, applicant_id", unique = true),
        @Index(name = "project_applications_project_id_idx", columnList = "project_id"),
        @Index(name = "project_applications_applicant_id_idx", columnList = "applicant_id"),
        @Index(name = "project_applications_status_idx", columnList = "status")
})
public class ProjectApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectPost project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Profile applicant;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @Column(name = "portfolio_url", length = 500)
    private String portfolioUrl;

    @Column(name = "github_url", length = 500)
    private String githubUrl;

    @ColumnDefault("now()")
    @Column(name = "applied_at")
    private Instant appliedAt;
    @Column(name = "responded_at")
    private Instant respondedAt;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'pending'")
    @Column(name = "status")
    private application_status status;

}