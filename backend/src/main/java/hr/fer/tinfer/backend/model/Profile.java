package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "photos", "departments", "interests" })
public class Profile {

    @Id
    private UUID id; // Reference to Supabase auth.users.id

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "display_name", length = 100)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Min(1)
    @Max(5)
    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    @Column(name = "student_id", unique = true, length = 50)
    private String studentId;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String gender; // male, female, non_binary, other

    @Column(name = "interested_in_gender", length = 50)
    private String interestedInGender; // male, female, non_binary, everyone

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @org.hibernate.annotations.BatchSize(size = 25)
    private Set<Photo> photos = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_departments", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "department_id"))
    @org.hibernate.annotations.BatchSize(size = 25)
    private Set<Department> departments = new HashSet<>();

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "interest_id"))
    @org.hibernate.annotations.BatchSize(size = 25)
    private Set<Interest> interests = new HashSet<>();
}
