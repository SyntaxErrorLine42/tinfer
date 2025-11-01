package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import hr.fer.tinfer.backend.types.swipe_action;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "dating_swipes", indexes = {
        @Index(name = "dating_swipes_swiper_id_swiped_id_idx", columnList = "swiper_id, swiped_id", unique = true),
        @Index(name = "dating_swipes_swiper_id_idx", columnList = "swiper_id"),
        @Index(name = "dating_swipes_swiped_id_idx", columnList = "swiped_id")
})
public class DatingSwipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiper_id")
    private Profile swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiped_id")
    private Profile swiped;

    @ColumnDefault("now()")
    @Column(name = "swiped_at")
    private Instant swipedAt;


     @Column(name = "action", columnDefinition = "swipe_action not null")
     private swipe_action action;

}