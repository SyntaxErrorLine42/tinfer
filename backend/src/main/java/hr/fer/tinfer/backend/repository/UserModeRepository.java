package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.UserMode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserModeRepository extends JpaRepository<UserMode, Integer> {
  }