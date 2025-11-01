package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Cours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursRepository extends JpaRepository<Cours, Integer> {
}