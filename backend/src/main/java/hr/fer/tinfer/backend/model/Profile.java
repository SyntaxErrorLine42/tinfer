package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "profiles", indexes = {
        @Index(name = "profiles_email_idx", columnList = "email"),
        @Index(name = "profiles_student_id_idx", columnList = "student_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "profiles_email_key", columnNames = { "email" }),
        @UniqueConstraint(name = "profiles_student_id_key", columnNames = { "student_id" })
})
public class Profile {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id", nullable = false)
    private User users;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(name = "bio", length = Integer.MAX_VALUE)
    private String bio;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    @Column(name = "student_id", length = 50)
    private String studentId;

    @ColumnDefault("false")
    @Column(name = "is_verified")
    private Boolean isVerified;

    @ColumnDefault("true")
    @Column(name = "is_active")
    private Boolean isActive;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at")
    private Instant updatedAt;
}