package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.MatchSummary;
import hr.fer.tinfer.backend.model.Department;
import hr.fer.tinfer.backend.model.Interest;
import hr.fer.tinfer.backend.model.Match;
import hr.fer.tinfer.backend.model.Photo;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.MatchRepository;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final ProfileRepository profileRepository;

    public List<MatchSummary> getMatches(UUID userId) {
        Profile currentUser = profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user " + userId));

        Set<String> currentUserInterests = extractInterestNames(currentUser.getInterests());
        Set<String> currentUserDepartments = extractDepartmentCodes(currentUser.getDepartments());

        return matchRepository.findAllByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(Match::getMatchedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(match -> toSummary(match, userId, currentUserInterests, currentUserDepartments))
                .collect(Collectors.toList());
    }

    private MatchSummary toSummary(Match match,
            UUID currentUserId,
            Set<String> currentUserInterests,
            Set<String> currentUserDepartments) {

        Profile partner = match.getUser1().getId().equals(currentUserId) ? match.getUser2() : match.getUser1();

        Set<String> partnerInterests = extractInterestNames(partner.getInterests());
        Set<String> sharedInterests = new LinkedHashSet<>(currentUserInterests);
        sharedInterests.retainAll(partnerInterests);

        Set<String> partnerDepartments = extractDepartmentCodes(partner.getDepartments());
        Set<String> sharedDepartments = new LinkedHashSet<>(currentUserDepartments);
        sharedDepartments.retainAll(partnerDepartments);

        Long conversationId = match.getConversation() != null ? match.getConversation().getId() : null;
        var conversationLastMessageAt = match.getConversation() != null ? match.getConversation().getLastMessageAt()
                : null;

        return MatchSummary.builder()
                .matchId(match.getId())
                .partnerId(partner.getId())
                .partnerFirstName(partner.getFirstName())
                .partnerLastName(partner.getLastName())
                .partnerDisplayName(partner.getDisplayName())
                .partnerBio(partner.getBio())
                .partnerYearOfStudy(partner.getYearOfStudy())
                .partnerVerified(partner.getIsVerified())
                .sharedInterests(sharedInterests)
                .partnerDepartments(partnerDepartments)
                .primaryPhotoBase64(resolvePrimaryPhoto(partner))
                .highlight(buildHighlight(sharedInterests, sharedDepartments, partner))
                .conversationId(conversationId)
                .conversationLastMessageAt(conversationLastMessageAt)
                .matchedAt(match.getMatchedAt())
                .build();
    }

    private Set<String> extractInterestNames(Set<Interest> interests) {
        return interests == null
                ? new LinkedHashSet<>()
                : interests.stream()
                        .map(Interest::getName)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> extractDepartmentCodes(Set<Department> departments) {
        return departments == null
                ? new LinkedHashSet<>()
                : departments.stream()
                        .map(Department::getCode)
                        .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String resolvePrimaryPhoto(Profile profile) {
        return profile.getPhotos()
                .stream()
                .sorted(Comparator
                        .comparing((Photo photo) -> Boolean.TRUE.equals(photo.getIsPrimary())).reversed()
                        .thenComparing(Photo::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Photo::getId, Comparator.nullsLast(Long::compareTo)))
                .map(Photo::getBase64Data)
                .findFirst()
                .orElse(null);
    }

    private String buildHighlight(Set<String> sharedInterests, Set<String> sharedDepartments, Profile partner) {
        if (!sharedInterests.isEmpty()) {
            List<String> topInterests = sharedInterests.stream().limit(2).toList();
            if (topInterests.size() == 1) {
                return "Oboje volite " + topInterests.get(0);
            }
            return "Dijelite interese: " + String.join(", ", topInterests);
        }

        if (!sharedDepartments.isEmpty()) {
            return "Isti odsjek: " + sharedDepartments.iterator().next();
        }

        if (Boolean.TRUE.equals(partner.getIsVerified())) {
            return "Provjeren ferovac";
        }

        return "Novi match na Tinferu";
    }
}
