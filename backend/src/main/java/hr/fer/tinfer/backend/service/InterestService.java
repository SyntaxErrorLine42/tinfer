package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.model.Interest;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.InterestRepository;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InterestService {

    private final InterestRepository interestRepository;
    private final ProfileRepository profileRepository;

    /**
     * Get all available interests
     */
    public List<Interest> getAllInterests() {
        return interestRepository.findAll();
    }

    /**
     * Search interests by name (for autocomplete)
     */
    public List<Interest> searchInterests(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllInterests();
        }
        return interestRepository.findByNameContainingIgnoreCase(query.trim());
    }

    /**
     * Get interests by category
     */
    public List<Interest> getInterestsByCategory(String category) {
        return interestRepository.findByCategory(category);
    }

    /**
     * Get or create interest by name
     */
    @Transactional
    public Interest getOrCreateInterest(String name) {
        return interestRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Interest newInterest = new Interest();
                    newInterest.setName(name.trim());
                    newInterest.setCategory("Other"); // Default category
                    return interestRepository.save(newInterest);
                });
    }

    /**
     * Add interests to user profile
     */
    @Transactional
    public void addInterestsToProfile(UUID userId, Set<String> interestNames) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        Set<Interest> interests = interestNames.stream()
                .map(this::getOrCreateInterest)
                .collect(Collectors.toSet());

        profile.getInterests().addAll(interests);
        profileRepository.save(profile);

        log.info("Added {} interests to profile {}", interests.size(), userId);
    }

    /**
     * Remove interest from user profile
     */
    @Transactional
    public void removeInterestFromProfile(UUID userId, String interestName) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        profile.getInterests().removeIf(interest -> interest.getName().equalsIgnoreCase(interestName));

        profileRepository.save(profile);

        log.info("Removed interest '{}' from profile {}", interestName, userId);
    }

    /**
     * Set user's interests (replaces existing ones)
     */
    @Transactional
    public void setProfileInterests(UUID userId, Set<String> interestNames) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

        Set<Interest> interests = interestNames.stream()
                .map(this::getOrCreateInterest)
                .collect(Collectors.toSet());

        profile.getInterests().clear();
        profile.getInterests().addAll(interests);
        profileRepository.save(profile);

        log.info("Set {} interests for profile {}", interests.size(), userId);
    }

    /**
     * Get user's interests
     */
    public Set<Interest> getUserInterests(UUID userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
        return profile.getInterests();
    }
}
