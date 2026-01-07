package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.dto.CreateProfileRequest;
import hr.fer.tinfer.backend.dto.PhotoCreateRequest;
import hr.fer.tinfer.backend.dto.PhotoResponse;
import hr.fer.tinfer.backend.dto.PhotoUpdateRequest;
import hr.fer.tinfer.backend.dto.ProfileDetailsResponse;
import hr.fer.tinfer.backend.dto.ProfileResponse;
import hr.fer.tinfer.backend.dto.UserActivityLogResponse;
import hr.fer.tinfer.backend.service.ProfileService;
import hr.fer.tinfer.backend.service.PhotoService;
import hr.fer.tinfer.backend.service.UserActivityLogService;
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
@Tag(name = "Profile", description = "Manage user profiles")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {

    private final ProfileService profileService;
    private final PhotoService photoService;
    private final UserActivityLogService activityLogService;

    @GetMapping("/me")
    @Operation(summary = "Get the profile of the currently logged-in user")
    public ResponseEntity<ProfileResponse> getCurrentUserProfile(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return profileService.getCurrentUserProfile(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get profile by ID")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable UUID id) {
        return profileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Detailed profile view with photos and interests")
    public ResponseEntity<ProfileDetailsResponse> getProfileDetails(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID requesterId = authentication != null ? (UUID) authentication.getPrincipal() : null;

        return profileService.getProfileDetails(requesterId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get profile by email")
    public ResponseEntity<ProfileResponse> getProfileByEmail(@PathVariable String email) {
        return profileService.getProfileByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all profiles")
    public ResponseEntity<List<ProfileResponse>> getAllProfiles() {
        List<ProfileResponse> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @PostMapping
    @Operation(summary = "Create new profile")
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
    @Operation(summary = "Update profile")
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
    @Operation(summary = "Delete profile")
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

    @GetMapping("/me/photos")
    @Operation(summary = "Get photos of the current user")
    public ResponseEntity<List<PhotoResponse>> getMyPhotos(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(photoService.getPhotos(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/me/photos")
    @Operation(summary = "Add a new photo to the profile")
    public ResponseEntity<PhotoResponse> addPhoto(
            @Valid @RequestBody PhotoCreateRequest request,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();
        try {
            PhotoResponse created = photoService.addPhoto(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/me/photos/{photoId}")
    @Operation(summary = "Update an existing photo")
    public ResponseEntity<PhotoResponse> updatePhoto(
            @PathVariable Long photoId,
            @Valid @RequestBody PhotoUpdateRequest request,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();

        try {
            return ResponseEntity.ok(photoService.updatePhoto(userId, photoId, request));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/me/photos/{photoId}")
    @Operation(summary = "Delete photo")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long photoId,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();

        try {
            photoService.deletePhoto(userId, photoId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/me/audit")
    @Operation(summary = "Get user activity audit trail")
    public ResponseEntity<List<UserActivityLogResponse>> getMyAuditTrail(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return ResponseEntity.ok(activityLogService.getActivityForUser(userId));
    }
}
