package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.ProjectRequiredSkill;
import hr.fer.tinfer.backend.model.ProjectRequiredSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRequiredSkillRepository extends JpaRepository<ProjectRequiredSkill, ProjectRequiredSkillId> {
}