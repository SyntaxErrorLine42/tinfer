package hr.fer.tinfer.backend.model;

import hr.fer.tinfer.backend.types.app_mode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "mode_preferences", indexes = {
        @Index(name = "mode_preferences_user_id_mode_idx", columnList = "user_id, mode", unique = true)
})
public class ModePreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;

    @Column(name = "preferences")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> preferences;
    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;


      @Column(name = "mode", columnDefinition = "app_mode not null")
      private app_mode mode;

}