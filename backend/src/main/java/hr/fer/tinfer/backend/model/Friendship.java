package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import hr.fer.tinfer.backend.types.friendship_status;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "friendships", indexes = {
        @Index(name = "friendships_requester_id_receiver_id_idx", columnList = "requester_id, receiver_id", unique = true),
        @Index(name = "friendships_requester_id_idx", columnList = "requester_id"),
        @Index(name = "friendships_receiver_id_idx", columnList = "receiver_id"),
        @Index(name = "friendships_status_idx", columnList = "status")
})
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private Profile requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Profile receiver;

    @ColumnDefault("now()")
    @Column(name = "requested_at")
    private Instant requestedAt;
    @Column(name = "responded_at")
    private Instant respondedAt;


     @Column(name = "status", columnDefinition = "friendship_status")
     private friendship_status status;

}