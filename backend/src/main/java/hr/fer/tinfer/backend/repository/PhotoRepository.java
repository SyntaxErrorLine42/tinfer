package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
}