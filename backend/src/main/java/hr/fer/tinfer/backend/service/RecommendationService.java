package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.ProfileRecommendation;
import hr.fer.tinfer.backend.model.Department;
import hr.fer.tinfer.backend.model.Interest;
import hr.fer.tinfer.backend.model.Photo;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.DatingSwipeRepository;
import hr.fer.tinfer.backend.repository.MatchRepository;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecommendationService {

    private static final int DEFAULT_LIMIT = 25;
    private static final double INTEREST_WEIGHT = 0.55;
    private static final double DEPARTMENT_WEIGHT = 0.25;
    private static final double YEAR_WEIGHT = 0.15;
    private static final double VERIFICATION_WEIGHT = 0.05;

    private final ProfileRepository profileRepository;
    private final DatingSwipeRepository datingSwipeRepository;
    private final MatchRepository matchRepository;

    public List<ProfileRecommendation> getRecommendations(UUID userId) {
        return getRecommendations(userId, DEFAULT_LIMIT);
    }

    public List<ProfileRecommendation> getRecommendations(UUID userId, int limit) {
        Profile currentUser = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user " + userId));

        Set<UUID> alreadySwiped = datingSwipeRepository.findBySwiper(currentUser)
                .stream()
                .map(swipe -> swipe.getSwiped().getId())
                .collect(Collectors.toSet());

        Set<UUID> matchedUsers = matchRepository.findAllByUserId(userId)
                .stream()
                .map(match -> match.getUser1().getId().equals(userId) ? match.getUser2().getId()
                        : match.getUser1().getId())
                .collect(Collectors.toSet());

        int effectiveLimit = Math.max(1, limit);

        return profileRepository.findAll()
                .stream()
                .filter(candidate -> !candidate.getId().equals(userId))
                .filter(this::isActive)
                .filter(candidate -> !alreadySwiped.contains(candidate.getId()))
                .filter(candidate -> !matchedUsers.contains(candidate.getId()))
                .map(candidate -> buildRecommendation(currentUser, candidate))
                .sorted(Comparator.comparingDouble(ProfileRecommendation::getCompatibilityScore).reversed())
                .limit(effectiveLimit)
                .collect(Collectors.toList());
    }

    private ProfileRecommendation buildRecommendation(Profile currentUser, Profile candidate) {
        Set<String> currentUserInterests = extractInterestNames(currentUser.getInterests());
        Set<String> candidateInterests = extractInterestNames(candidate.getInterests());

        Set<String> sharedInterests = new LinkedHashSet<>(currentUserInterests);
        sharedInterests.retainAll(candidateInterests);

        Set<String> sharedDepartments = extractDepartmentCodes(currentUser.getDepartments());
        sharedDepartments.retainAll(extractDepartmentCodes(candidate.getDepartments()));

        double compatibilityScore = roundScore(
                INTEREST_WEIGHT * calculateJaccardScore(currentUserInterests, candidateInterests)
                        + DEPARTMENT_WEIGHT * calculateDepartmentScore(currentUser, candidate)
                        + YEAR_WEIGHT * calculateYearScore(currentUser.getYearOfStudy(), candidate.getYearOfStudy())
                        + VERIFICATION_WEIGHT * (Boolean.TRUE.equals(candidate.getIsVerified()) ? 1.0 : 0.0));

        String highlight = buildHighlight(sharedInterests, sharedDepartments, candidate);

        return ProfileRecommendation.builder()
                .profileId(candidate.getId())
                .firstName(candidate.getFirstName())
                .lastName(candidate.getLastName())
                .displayName(Optional.ofNullable(candidate.getDisplayName()).orElse(candidate.getFirstName()))
                .bio(candidate.getBio())
                .yearOfStudy(candidate.getYearOfStudy())
                .verified(candidate.getIsVerified())
                .sharedInterests(sharedInterests)
                .candidateInterests(candidateInterests)
                .departments(extractDepartmentCodes(candidate.getDepartments()))
                .primaryPhotoUrl(resolvePrimaryPhoto(candidate))
                .photoGallery(extractPhotoGallery(candidate))
                .compatibilityScore(compatibilityScore)
                .highlight(highlight)
                .build();
    }

    private boolean isActive(Profile profile) {
        return profile.getIsActive() == null || Boolean.TRUE.equals(profile.getIsActive());
    }

    private Set<String> extractInterestNames(Set<Interest> interests) {
        return Optional.ofNullable(interests)
                .orElseGet(LinkedHashSet::new)
                .stream()
                .map(Interest::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> extractDepartmentCodes(Set<Department> departments) {
        return Optional.ofNullable(departments)
                .orElseGet(LinkedHashSet::new)
                .stream()
                .map(Department::getCode)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private double calculateJaccardScore(Set<String> interestsA, Set<String> interestsB) {
        if (interestsA.isEmpty() && interestsB.isEmpty()) {
            return 0.2; // slight boost when no data yet
        }

        Set<String> intersection = new LinkedHashSet<>(interestsA);
        intersection.retainAll(interestsB);

        Set<String> union = new LinkedHashSet<>(interestsA);
        union.addAll(interestsB);

        if (union.isEmpty()) {
            return 0.0;
        }

        return (double) intersection.size() / union.size();
    }

    private double calculateDepartmentScore(Profile currentUser, Profile candidate) {
        if (currentUser.getDepartments() == null || currentUser.getDepartments().isEmpty()) {
            return 0.0;
        }

        Set<String> mine = extractDepartmentCodes(currentUser.getDepartments());
        Set<String> theirs = extractDepartmentCodes(candidate.getDepartments());

        if (theirs.isEmpty()) {
            return 0.0;
        }

        mine.retainAll(theirs);
        return mine.isEmpty() ? 0.0 : 1.0;
    }

    private double calculateYearScore(Integer yearA, Integer yearB) {
        if (yearA == null || yearB == null) {
            return 0.5; // neutral when data missing
        }

        int distance = Math.abs(yearA - yearB);
        if (distance == 0) {
            return 1.0;
        }
        if (distance == 1) {
            return 0.75;
        }
        if (distance == 2) {
            return 0.4;
        }
        return 0.2;
    }

    private double roundScore(double score) {
        return Math.round(Math.min(1.0, Math.max(0.0, score)) * 100.0) / 100.0;
    }

    private String resolvePrimaryPhoto(Profile candidate) {
        return candidate.getPhotos()
                .stream()
                .sorted(Comparator
                        .comparing((Photo photo) -> Boolean.TRUE.equals(photo.getIsPrimary())).reversed()
                        .thenComparing(Photo::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Photo::getId, Comparator.nullsLast(Long::compareTo)))
                .map(Photo::getUrl)
                .findFirst()
                .orElse(null);
    }

    private List<String> extractPhotoGallery(Profile candidate) {
        return candidate.getPhotos()
                .stream()
                .sorted(Comparator
                        .comparing(Photo::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Photo::getId, Comparator.nullsLast(Long::compareTo)))
                .map(Photo::getUrl)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String buildHighlight(Set<String> sharedInterests, Set<String> sharedDepartments, Profile candidate) {
        if (!sharedInterests.isEmpty()) {
            List<String> topInterests = sharedInterests.stream().limit(2).toList();
            if (topInterests.size() == 1) {
                return "Oboje volite " + topInterests.get(0);
            }
            return "Dijelite interese: " + String.join(", ", topInterests);
        }

        if (!sharedDepartments.isEmpty()) {
            return "Studira na istom odsjeku (" + sharedDepartments.iterator().next() + ")";
        }

        if (Boolean.TRUE.equals(candidate.getIsVerified())) {
            return "Provjeren ferovac";
        }

        return "Aktivan profil s FER-a";
    }
}
