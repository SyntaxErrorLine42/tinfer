package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.DatingSwipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatingSwipeRepository extends JpaRepository<DatingSwipe, Integer> {
  }