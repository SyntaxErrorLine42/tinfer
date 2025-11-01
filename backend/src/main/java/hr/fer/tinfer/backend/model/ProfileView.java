package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import hr.fer.tinfer.backend.types.app_mode;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "profile_views", indexes = {
        @Index(name = "profile_views_viewer_id_idx", columnList = "viewer_id"),
        @Index(name = "profile_views_viewed_id_mode_idx", columnList = "viewed_id, mode")
})
public class ProfileView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id")
    private Profile viewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewed_id")
    private Profile viewed;

    @ColumnDefault("now()")
    @Column(name = "viewed_at")
    private Instant viewedAt;


     @Column(name = "mode", columnDefinition = "app_mode not null")
     private app_mode mode;

}