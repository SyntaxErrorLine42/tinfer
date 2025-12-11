package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE (m.user1.id = :userId OR m.user2.id = :userId)")
    List<Match> findAllByUserId(UUID userId);

    @Query("SELECT m FROM Match m WHERE (m.user1.id = :user1Id AND m.user2.id = :user2Id) OR (m.user1.id = :user2Id AND m.user2.id = :user1Id)")
    Optional<Match> findByUsers(UUID user1Id, UUID user2Id);
}
