package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
  }