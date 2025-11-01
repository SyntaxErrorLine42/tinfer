package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "courses", indexes = {
        @Index(name = "courses_code_idx", columnList = "code"),
        @Index(name = "courses_department_id_idx", columnList = "department_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "courses_code_key", columnNames = { "code" })
})
public class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "ects")
    private Integer ects;

    @OneToMany(mappedBy = "course")
    private Set<UserCours> userCourses = new LinkedHashSet<>();
}