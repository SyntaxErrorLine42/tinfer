package hr.fer.tinfer.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "departments", uniqueConstraints = {
        @UniqueConstraint(name = "departments_code_key", columnNames = { "code" })
})
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @OneToMany(mappedBy = "department")
    private Set<Cours> courses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "department")
    private Set<UserDepartment> userDepartments = new LinkedHashSet<>();
}