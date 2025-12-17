package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.CreateProfileRequest;
import hr.fer.tinfer.backend.dto.PhotoResponse;
import hr.fer.tinfer.backend.dto.ProfileDetailsResponse;
import hr.fer.tinfer.backend.dto.ProfileResponse;
import hr.fer.tinfer.backend.model.Photo;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserActivityLogService activityLogService;

    public Optional<ProfileResponse> getCurrentUserProfile(UUID userId) {
        log.debug("Fetching profile for user ID: {}", userId);
        return profileRepository.findById(userId)
                .map(this::toResponse);
    }

    public Optional<ProfileResponse> getProfileById(UUID id) {
        log.debug("Fetching profile by ID: {}", id);
        return profileRepository.findById(id)
                .map(this::toResponse);
    }

    public Optional<ProfileResponse> getProfileByEmail(String email) {
        log.debug("Fetching profile by email: {}", email);
        return profileRepository.findByEmail(email)
                .map(this::toResponse);
    }

    public List<ProfileResponse> getAllProfiles() {
        log.debug("Fetching all profiles");
        return profileRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<ProfileDetailsResponse> getProfileDetails(UUID requesterId, UUID profileId) {
        log.debug("Fetching detailed profile info for {} by {}", profileId, requesterId);

        return profileRepository.findById(profileId)
                .map(profile -> {
                    if (requesterId != null && !requesterId.equals(profileId)) {
                        Map<String, Object> metadata = new HashMap<>();
                        metadata.put("profileId", profileId.toString());
                        activityLogService.recordActivity(requesterId, "PROFILE_VIEW", metadata);
                    }
                    return toDetailsResponse(profile);
                });
    }

    @Transactional
    public ProfileResponse createProfile(CreateProfileRequest request, UUID userId) {
        log.info("Creating profile for user ID: {}", userId);

        if (profileRepository.existsById(userId)) {
            log.warn("Profile already exists for user ID: {}", userId);
            throw new IllegalStateException("Profile already exists for this user");
        }

        if (profileRepository.existsByEmail(request.getEmail())) {
            log.warn("Profile with email {} already exists", request.getEmail());
            throw new IllegalStateException("Profile with this email already exists");
        }

        Profile profile = new Profile();
        profile.setId(userId);
        profile.setEmail(request.getEmail());
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setDisplayName(request.getDisplayName());
        profile.setBio(request.getBio());
        profile.setYearOfStudy(request.getYearOfStudy());
        profile.setStudentId(request.getStudentId());
        profile.setGender(request.getGender());
        profile.setInterestedInGender(request.getInterestedInGender());
        profile.setIsVerified(false);
        profile.setIsActive(true);

        Profile savedProfile = profileRepository.save(profile);
        log.info("Profile created successfully for user ID: {}", userId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("profileId", savedProfile.getId().toString());
        activityLogService.recordActivity(userId, "PROFILE_CREATED", metadata);

        return toResponse(savedProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(UUID id, CreateProfileRequest request, UUID currentUserId) {
        log.info("Updating profile ID: {} by user ID: {}", id, currentUserId);

        if (!currentUserId.equals(id)) {
            log.warn("User {} attempted to update profile {}", currentUserId, id);
            throw new SecurityException("You can only update your own profile");
        }

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Profile not found with ID: {}", id);
                    return new IllegalArgumentException("Profile not found");
                });

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setDisplayName(request.getDisplayName());
        profile.setBio(request.getBio());
        profile.setYearOfStudy(request.getYearOfStudy());
        profile.setStudentId(request.getStudentId());
        profile.setGender(request.getGender());
        profile.setInterestedInGender(request.getInterestedInGender());

        Profile updatedProfile = profileRepository.save(profile);
        log.info("Profile updated successfully: {}", id);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("profileId", updatedProfile.getId().toString());
        activityLogService.recordActivity(currentUserId, "PROFILE_UPDATED", metadata);

        return toResponse(updatedProfile);
    }

    @Transactional
    public void deleteProfile(UUID id, UUID currentUserId) {
        log.info("Deleting profile ID: {} by user ID: {}", id, currentUserId);

        // Provjeri da korisnik briše svoj profil
        if (!currentUserId.equals(id)) {
            log.warn("User {} attempted to delete profile {}", currentUserId, id);
            throw new SecurityException("You can only delete your own profile");
        }

        if (!profileRepository.existsById(id)) {
            log.error("Profile not found with ID: {}", id);
            throw new IllegalArgumentException("Profile not found");
        }

        profileRepository.deleteById(id);
        log.info("Profile deleted successfully: {}", id);
    }

    public boolean profileExists(UUID userId) {
        return profileRepository.existsById(userId);
    }

    private ProfileDetailsResponse toDetailsResponse(Profile profile) {
        log.debug("Converting profile {} to details response", profile.getId());
        log.debug("Profile interests: {}", profile.getInterests());

        List<String> interests = profile.getInterests()
                .stream()
                .map(interest -> interest.getName())
                .sorted()
                .collect(Collectors.toList());

        log.debug("Mapped interests: {}", interests);

        List<String> departments = profile.getDepartments()
                .stream()
                .map(department -> department.getName())
                .sorted()
                .collect(Collectors.toList());

        return ProfileDetailsResponse.builder()
                .id(profile.getId())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .displayName(profile.getDisplayName())
                .bio(profile.getBio())
                .yearOfStudy(profile.getYearOfStudy())
                .studentId(profile.getStudentId())
                .gender(profile.getGender())
                .interestedInGender(profile.getInterestedInGender())
                .isVerified(profile.getIsVerified())
                .isActive(profile.getIsActive())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .interests(interests)
                .departments(departments)
                .photos(mapPhotos(profile))
                .build();
    }

    private List<PhotoResponse> mapPhotos(Profile profile) {
        return profile.getPhotos()
                .stream()
                .sorted(photoComparator())
                .map(photo -> PhotoResponse.builder()
                        .id(photo.getId())
                        .base64Data(photo.getBase64Data())
                        .displayOrder(photo.getDisplayOrder())
                        .isPrimary(photo.getIsPrimary())
                        .uploadedAt(photo.getUploadedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private Comparator<Photo> photoComparator() {
        return Comparator
                .comparing((Photo photo) -> Boolean.TRUE.equals(photo.getIsPrimary())).reversed()
                .thenComparing(Photo::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(Photo::getId, Comparator.nullsLast(Long::compareTo));
    }

    private ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getDisplayName(),
                profile.getBio(),
                profile.getYearOfStudy(),
                profile.getStudentId(),
                profile.getGender(),
                profile.getInterestedInGender(),
                profile.getIsVerified(),
                profile.getIsActive(),
                profile.getCreatedAt());
    }
}
