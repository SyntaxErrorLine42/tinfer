package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.dto.CreateProfileRequest;
import hr.fer.tinfer.backend.dto.ProfileResponse;
import hr.fer.tinfer.backend.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Upravljanje korisničkim profilima")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(summary = "Dohvati profil trenutno prijavljenog korisnika")
    public ResponseEntity<ProfileResponse> getCurrentUserProfile(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return profileService.getCurrentUserProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Dohvati profil po ID-u")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable UUID id) {
        return profileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Dohvati profil po email-u")
    public ResponseEntity<ProfileResponse> getProfileByEmail(@PathVariable String email) {
        return profileService.getProfileByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Dohvati sve profile")
    public ResponseEntity<List<ProfileResponse>> getAllProfiles() {
        List<ProfileResponse> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @PostMapping
    @Operation(summary = "Kreiraj novi profil")
    public ResponseEntity<ProfileResponse> createProfile(
            @Valid @RequestBody CreateProfileRequest request,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();

        try {
            ProfileResponse profile = profileService.createProfile(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (IllegalStateException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Ažuriraj profil")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProfileRequest request,
            Authentication authentication) {

        UUID currentUserId = (UUID) authentication.getPrincipal();

        try {
            ProfileResponse profile = profileService.updateProfile(id, request, currentUserId);
            return ResponseEntity.ok(profile);
        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Obriši profil")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID currentUserId = (UUID) authentication.getPrincipal();

        try {
            profileService.deleteProfile(id, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        }
    }
}
