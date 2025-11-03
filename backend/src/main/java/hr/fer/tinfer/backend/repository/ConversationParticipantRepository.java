package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.ConversationParticipant;
import hr.fer.tinfer.backend.model.ConversationParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, ConversationParticipantId> {
  }