package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import java.time.Instant;
import hr.fer.tinfer.backend.types.app_mode;

@Getter
@Setter
@Entity
@Table(name = "user_modes", indexes = {
        @Index(name = "user_modes_user_id_mode_idx", columnList = "user_id, mode", unique = true),
        @Index(name = "user_modes_user_id_idx", columnList = "user_id")
})
public class UserMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("now()")
    @Column(name = "joined_at")
    private Instant joinedAt;
    @Column(name = "paused_at")
    private Instant pausedAt;
    @Column(name = "mode", columnDefinition = "app_mode not null")
    private app_mode mode;

}