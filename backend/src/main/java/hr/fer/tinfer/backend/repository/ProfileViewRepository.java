package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.ProfileView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileViewRepository extends JpaRepository<ProfileView, Integer> {
  }