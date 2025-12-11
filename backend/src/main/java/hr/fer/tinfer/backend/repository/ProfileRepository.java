package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByStudentId(String studentId);

    boolean existsByEmail(String email);
}
