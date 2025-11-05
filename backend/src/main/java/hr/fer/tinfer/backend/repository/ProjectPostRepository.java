package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.ProjectPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectPostRepository extends JpaRepository<ProjectPost, Integer> {
  }