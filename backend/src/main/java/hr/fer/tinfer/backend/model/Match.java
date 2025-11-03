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
@Table(name = "matches", schema = "public", indexes = {
        @Index(name = "matches_user1_id_user2_id_match_type_idx", columnList = "user1_id, user2_id, match_type", unique = true),
        @Index(name = "matches_user1_id_idx", columnList = "user1_id"),
        @Index(name = "matches_user2_id_idx", columnList = "user2_id"),
        @Index(name = "matches_match_type_idx", columnList = "match_type")
})
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    private Profile user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    private Profile user2;

    @ColumnDefault("now()")
    @Column(name = "matched_at")
    private Instant matchedAt;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProjectPost project;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type",nullable = false)
    private match_type matchType;

}