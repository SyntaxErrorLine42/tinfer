package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByStudentId(String studentId);

    boolean existsByEmail(String email);

    /**
     * Find profile with photos eagerly loaded (avoids N+1 for photos)
     */
    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.photos WHERE p.id = :id")
    Optional<Profile> findByIdWithPhotos(@Param("id") UUID id);

    /**
     * Find all active profiles excluding specific IDs, with photos pre-fetched
     * This avoids N+1 queries when loading recommendations
     */
    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.photos WHERE p.isActive = true AND p.id NOT IN :excludeIds")
    List<Profile> findActiveProfilesWithPhotosExcluding(@Param("excludeIds") Set<UUID> excludeIds);

    /**
     * Find all active profiles with photos (for when no exclusions needed)
     */
    @Query("SELECT DISTINCT p FROM Profile p LEFT JOIN FETCH p.photos WHERE p.isActive = true AND p.id <> :userId")
    List<Profile> findActiveProfilesWithPhotosExcludingUser(@Param("userId") UUID userId);
}
