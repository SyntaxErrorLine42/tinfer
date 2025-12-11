package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.DatingSwipe;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.types.SwipeAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DatingSwipeRepository extends JpaRepository<DatingSwipe, Long> {
    Optional<DatingSwipe> findBySwiperAndSwiped(Profile swiper, Profile swiped);

    List<DatingSwipe> findBySwiper(Profile swiper);

    @Query("SELECT s FROM DatingSwipe s WHERE s.swiper.id = :swiperId AND s.action = :action")
    List<DatingSwipe> findBySwiperIdAndAction(UUID swiperId, SwipeAction action);
}
