package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}