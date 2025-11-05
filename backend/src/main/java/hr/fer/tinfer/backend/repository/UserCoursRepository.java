package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.UserCours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCoursRepository extends JpaRepository<UserCours, Integer> {
  }