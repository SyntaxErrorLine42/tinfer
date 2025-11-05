package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}