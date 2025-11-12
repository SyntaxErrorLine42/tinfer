package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.Photo;
import hr.fer.tinfer.backend.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByUserOrderByDisplayOrderAsc(Profile user);

    Optional<Photo> findByUserAndIsPrimaryTrue(Profile user);
}
