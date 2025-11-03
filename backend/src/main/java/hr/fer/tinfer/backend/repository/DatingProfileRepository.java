package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.DatingProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DatingProfileRepository extends JpaRepository<DatingProfile, UUID> {

  }