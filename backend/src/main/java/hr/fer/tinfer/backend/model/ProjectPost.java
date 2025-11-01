package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import hr.fer.tinfer.backend.types.*;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "project_posts", indexes = {
        @Index(name = "project_posts_posted_by_idx", columnList = "posted_by"),
        @Index(name = "idx_project_posts_active", columnList = "is_active"),
        @Index(name = "project_posts_created_at_idx", columnList = "created_at")
})
public class ProjectPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by")
    private Profile postedBy;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "deadline")
    private LocalDate deadline;

    @ColumnDefault("false")
    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(name = "compensation_details", length = Integer.MAX_VALUE)
    private String compensationDetails;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;
    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "expires_at")
    private Instant expiresAt;


    @Column(name = "project_type", columnDefinition = "project_type not null")
    private project_type projectType;


     @Column(name = "commitment_level", columnDefinition = "commitment_level")
    private commitment_level commitmentLevel;

}