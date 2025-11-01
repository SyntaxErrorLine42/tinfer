package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.ProjectApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Integer> {
}