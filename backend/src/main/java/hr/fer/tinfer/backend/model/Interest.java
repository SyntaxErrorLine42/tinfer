package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "interests", uniqueConstraints = {
        @UniqueConstraint(name = "interests_name_key", columnNames = { "name" })
})
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "category", length = 50)
    private String category;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToMany
    @JoinTable(name = "user_interests", joinColumns = @JoinColumn(name = "interest_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Profile> profiles = new LinkedHashSet<>();
}