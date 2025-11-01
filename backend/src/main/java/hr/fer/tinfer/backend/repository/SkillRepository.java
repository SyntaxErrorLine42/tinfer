package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
}