package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Integer> {
  }