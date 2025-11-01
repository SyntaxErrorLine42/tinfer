package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Integer> {
}