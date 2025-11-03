package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.ProjectProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectProfileRepository extends JpaRepository<ProjectProfile, Integer> {
  }