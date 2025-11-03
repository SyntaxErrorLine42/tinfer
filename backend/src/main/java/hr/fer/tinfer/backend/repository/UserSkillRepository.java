package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSkillRepository extends JpaRepository<UserSkill, Integer> {
  }