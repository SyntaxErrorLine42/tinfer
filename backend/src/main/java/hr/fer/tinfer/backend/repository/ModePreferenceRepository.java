package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.ModePreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModePreferenceRepository extends JpaRepository<ModePreference, Integer> {
  }