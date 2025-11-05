package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Integer> {
  }