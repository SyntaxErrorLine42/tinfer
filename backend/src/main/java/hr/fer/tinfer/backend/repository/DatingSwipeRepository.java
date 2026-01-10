package hr.fer.tinfer.backend.repository;

import hr.fer.tinfer.backend.model.DatingSwipe;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.types.SwipeAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface DatingSwipeRepository extends JpaRepository<DatingSwipe, Long> {
    Optional<DatingSwipe> findBySwiperAndSwiped(Profile swiper, Profile swiped);

    List<DatingSwipe> findBySwiper(Profile swiper);

    @Query("SELECT s FROM DatingSwipe s WHERE s.swiper.id = :swiperId AND s.action = :action")
    List<DatingSwipe> findBySwiperIdAndAction(UUID swiperId, SwipeAction action);

    /**
     * Get only the IDs of profiles that the user has already swiped on.
     * This avoids loading full Profile entities just to get IDs.
     */
    @Query("SELECT s.swiped.id FROM DatingSwipe s WHERE s.swiper.id = :swiperId")
    Set<UUID> findSwipedIdsBySwiperId(@Param("swiperId") UUID swiperId);
}
