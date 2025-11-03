package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import hr.fer.tinfer.backend.types.app_mode;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "user_activity_log", schema = "public", indexes = {
        @Index(name = "idx_user_activity_user", columnList = "user_id, created_at"),
        @Index(name = "user_activity_log_activity_type_idx", columnList = "activity_type")
})
public class UserActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;

    @Column(name = "activity_type", length = 50)
    private String activityType;

    @Column(name = "metadata")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> metadata;
    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;


    @Enumerated(EnumType.STRING)
    @Column(name = "mode")
    private app_mode mode;

}