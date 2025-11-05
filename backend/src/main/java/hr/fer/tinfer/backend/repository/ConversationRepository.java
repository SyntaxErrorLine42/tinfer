package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
  }