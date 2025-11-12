package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Optional<Profile> findById(UUID id) {
        return profileRepository.findById(id);
    }

    public Optional<Profile> findByEmail(String email) {
        return profileRepository.findByEmail(email);
    }

    public Profile createProfile(Profile profile) {
        log.info("Creating new profile for user: {}", profile.getEmail());
        return profileRepository.save(profile);
    }

    public Profile updateProfile(Profile profile) {
        log.info("Updating profile: {}", profile.getId());
        return profileRepository.save(profile);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public void deleteProfile(UUID id) {
        log.info("Deleting profile: {}", id);
        profileRepository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return profileRepository.existsByEmail(email);
    }
}
