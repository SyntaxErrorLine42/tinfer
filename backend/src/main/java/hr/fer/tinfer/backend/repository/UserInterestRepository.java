package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.UserInterest;
import hr.fer.tinfer.backend.model.UserInterestId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId> {
}