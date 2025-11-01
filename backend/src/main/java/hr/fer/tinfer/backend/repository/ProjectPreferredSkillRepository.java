package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.ProjectPreferredSkill;
import hr.fer.tinfer.backend.model.ProjectPreferredSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectPreferredSkillRepository extends JpaRepository<ProjectPreferredSkill, ProjectPreferredSkillId> {
}