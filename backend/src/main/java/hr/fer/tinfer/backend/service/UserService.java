package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.CreateProfileRequest;
import hr.fer.tinfer.backend.dto.ProfileResponse;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final ProfileRepository profileRepository;

    /**
     * Dohvaća profil trenutno prijavljenog korisnika po ID-u
     */
    public Optional<ProfileResponse> getCurrentUserProfile(UUID userId) {
        log.debug("Fetching profile for user ID: {}", userId);
        return profileRepository.findById(userId)
                .map(this::toResponse);
    }

    /**
     * Dohvaća profil po ID-u
     */
    public Optional<ProfileResponse> getProfileById(UUID id) {
        log.debug("Fetching profile by ID: {}", id);
        return profileRepository.findById(id)
                .map(this::toResponse);
    }

    /**
     * Dohvaća profil po email-u
     */
    public Optional<ProfileResponse> getProfileByEmail(String email) {
        log.debug("Fetching profile by email: {}", email);
        return profileRepository.findByEmail(email)
                .map(this::toResponse);
    }

    /**
     * Dohvaća sve profile
     */
    public List<ProfileResponse> getAllProfiles() {
        log.debug("Fetching all profiles");
        return profileRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Kreira novi profil
     * 
     * @param request podaci za kreiranje profila
     * @param userId  ID korisnika iz JWT tokena (Supabase user ID)
     * @return kreirani profil
     * @throws IllegalStateException ako profil već postoji
     */
    @Transactional
    public ProfileResponse createProfile(CreateProfileRequest request, UUID userId) {
        log.info("Creating profile for user ID: {}", userId);

        // Provjeri da li profil već postoji
        if (profileRepository.existsById(userId)) {
            log.warn("Profile already exists for user ID: {}", userId);
            throw new IllegalStateException("Profile already exists for this user");
        }

        // Provjeri da li email već postoji
        if (profileRepository.existsByEmail(request.getEmail())) {
            log.warn("Profile with email {} already exists", request.getEmail());
            throw new IllegalStateException("Profile with this email already exists");
        }

        Profile profile = new Profile();
        profile.setId(userId); // ID iz Supabase auth.users
        profile.setEmail(request.getEmail());
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setDisplayName(request.getDisplayName());
        profile.setBio(request.getBio());
        profile.setYearOfStudy(request.getYearOfStudy());
        profile.setStudentId(request.getStudentId());
        profile.setIsVerified(false);
        profile.setIsActive(true);

        Profile savedProfile = profileRepository.save(profile);
        log.info("Profile created successfully for user ID: {}", userId);

        return toResponse(savedProfile);
    }

    /**
     * Ažurira postojeći profil
     * 
     * @param id            ID profila koji se ažurira
     * @param request       podaci za ažuriranje
     * @param currentUserId ID trenutno prijavljenog korisnika
     * @return ažurirani profil
     * @throws IllegalArgumentException ako profil ne postoji
     * @throws SecurityException        ako korisnik pokušava ažurirati tuđi profil
     */
    @Transactional
    public ProfileResponse updateProfile(UUID id, CreateProfileRequest request, UUID currentUserId) {
        log.info("Updating profile ID: {} by user ID: {}", id, currentUserId);

        // Provjera da korisnik može ažurirati samo svoj profil
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

        Profile updatedProfile = profileRepository.save(profile);
        log.info("Profile updated successfully: {}", id);

        return toResponse(updatedProfile);
    }

    /**
     * Briše profil
     * 
     * @param id            ID profila koji se briše
     * @param currentUserId ID trenutno prijavljenog korisnika
     * @throws IllegalArgumentException ako profil ne postoji
     * @throws SecurityException        ako korisnik pokušava obrisati tuđi profil
     */
    @Transactional
    public void deleteProfile(UUID id, UUID currentUserId) {
        log.info("Deleting profile ID: {} by user ID: {}", id, currentUserId);

        // Provjera da korisnik može obrisati samo svoj profil
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

    /**
     * Provjerava da li profil postoji za danog korisnika
     */
    public boolean profileExists(UUID userId) {
        return profileRepository.existsById(userId);
    }

    /**
     * Konvertira Profile entitet u ProfileResponse DTO
     */
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
                profile.getIsVerified(),
                profile.getIsActive(),
                profile.getCreatedAt());
    }
}
