package hr.fer.tinfer.backend.model;

import hr.fer.tinfer.backend.types.SwipeAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "dating_swipes", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "swiper_id", "swiped_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatingSwipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiper_id", nullable = false)
    private Profile swiper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "swiped_id", nullable = false)
    private Profile swiped;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwipeAction action;

    @CreationTimestamp
    @Column(name = "swiped_at", updatable = false)
    private LocalDateTime swipedAt;
}
