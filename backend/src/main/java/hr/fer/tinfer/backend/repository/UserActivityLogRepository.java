package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {

    List<UserActivityLog> findByUser_IdOrderByCreatedAtDesc(UUID userId);
}
