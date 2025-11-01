package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "user_courses", indexes = {
        @Index(name = "user_courses_user_id_course_id_semester_taken_idx", columnList = "user_id, course_id, semester_taken", unique = true),
        @Index(name = "idx_user_courses_current", columnList = "user_id, is_current"),
        @Index(name = "user_courses_user_id_idx", columnList = "user_id")
})
public class UserCours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Cours course;

    @Column(name = "semester_taken", length = 20)
    private String semesterTaken;

    @ColumnDefault("false")
    @Column(name = "is_current")
    private Boolean isCurrent;

    @Column(name = "grade", precision = 3, scale = 2)
    private BigDecimal grade;
}