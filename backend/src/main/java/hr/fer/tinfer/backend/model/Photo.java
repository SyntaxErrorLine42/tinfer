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
@Table(name = "photos", indexes = {
        @Index(name = "photos_user_id_is_primary_idx", columnList = "user_id, is_primary", unique = true),
        @Index(name = "photos_user_id_idx", columnList = "user_id"),
        @Index(name = "photos_mode_idx", columnList = "mode")
})
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @ColumnDefault("0")
    @Column(name = "display_order")
    private Integer displayOrder;

    @ColumnDefault("false")
    @Column(name = "is_primary")
    private Boolean isPrimary;

    @ColumnDefault("now()")
    @Column(name = "uploaded_at")
    private Instant uploadedAt;


     @Column(name = "mode", columnDefinition = "app_mode")
     private app_mode mode;

}