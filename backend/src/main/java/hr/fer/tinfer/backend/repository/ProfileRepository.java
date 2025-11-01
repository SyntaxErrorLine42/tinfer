package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}