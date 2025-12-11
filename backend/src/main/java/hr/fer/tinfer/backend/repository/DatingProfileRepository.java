package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.DatingProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DatingProfileRepository extends JpaRepository<DatingProfile, UUID> {
}
