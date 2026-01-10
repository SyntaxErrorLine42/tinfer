package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.PhotoCreateRequest;
import hr.fer.tinfer.backend.dto.PhotoResponse;
import hr.fer.tinfer.backend.dto.PhotoUpdateRequest;
import hr.fer.tinfer.backend.model.Photo;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.repository.PhotoRepository;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final ProfileRepository profileRepository;
    private final PhotoRepository photoRepository;
    private final UserActivityLogService activityLogService;
    private final SupabaseStorageService storageService;

    @Transactional(readOnly = true)
    public List<PhotoResponse> getPhotos(UUID userId) {
        Profile profile = fetchProfile(userId);
        return profile.getPhotos()
                .stream()
                .sorted(photoComparator())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PhotoResponse addPhoto(UUID userId, PhotoCreateRequest request) {
        Profile profile = fetchProfile(userId);
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            unsetExistingPrimary(profile);
        }

        // Upload to Supabase Storage and get URL
        String imageUrl = storageService.uploadImage(request.getBase64Data(), userId);

        Photo photo = new Photo();
        photo.setUser(profile);
        photo.setStorageUrl(imageUrl);
        photo.setDisplayOrder(request.getDisplayOrder());
        photo.setIsPrimary(Boolean.TRUE.equals(request.getIsPrimary()));

        Photo saved = photoRepository.save(photo);
        profile.getPhotos().add(saved);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("photoId", saved.getId());
        activityLogService.recordActivity(userId, "PHOTO_ADDED", metadata);

        return toResponse(saved);
    }

    @Transactional
    public PhotoResponse updatePhoto(UUID userId, Long photoId, PhotoUpdateRequest request) {
        Photo photo = fetchPhoto(photoId, userId);

        if (request.getBase64Data() != null) {
            // Delete old image from storage if it's a URL
            if (photo.isStorageUrl()) {
                storageService.deleteImage(photo.getStorageUrl());
            }
            // Upload new image
            String newUrl = storageService.uploadImage(request.getBase64Data(), userId);
            photo.setStorageUrl(newUrl);
        }
        if (request.getDisplayOrder() != null) {
            photo.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsPrimary() != null) {
            if (Boolean.TRUE.equals(request.getIsPrimary())) {
                unsetExistingPrimary(photo.getUser());
            }
            photo.setIsPrimary(request.getIsPrimary());
        }

        Photo saved = photoRepository.save(photo);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("photoId", saved.getId());
        metadata.put("isPrimary", saved.getIsPrimary());
        metadata.put("displayOrder", saved.getDisplayOrder());
        activityLogService.recordActivity(userId, "PHOTO_UPDATED", metadata);

        return toResponse(saved);
    }

    @Transactional
    public void deletePhoto(UUID userId, Long photoId) {
        Photo photo = fetchPhoto(photoId, userId);

        // Delete from Supabase Storage if it's a URL
        if (photo.isStorageUrl()) {
            storageService.deleteImage(photo.getStorageUrl());
        }

        photoRepository.delete(photo);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("photoId", photo.getId());
        activityLogService.recordActivity(userId, "PHOTO_DELETED", metadata);
    }

    private Photo fetchPhoto(Long photoId, UUID userId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

        if (!photo.getUser().getId().equals(userId)) {
            throw new SecurityException("Access to photo denied");
        }

        return photo;
    }

    private Profile fetchProfile(UUID userId) {
        return profileRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found"));
    }

    private Comparator<Photo> photoComparator() {
        return Comparator
                .comparing((Photo photo) -> Boolean.TRUE.equals(photo.getIsPrimary())).reversed()
                .thenComparing(Photo::getDisplayOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(Photo::getId, Comparator.nullsLast(Long::compareTo));
    }

    private void unsetExistingPrimary(Profile profile) {
        profile.getPhotos().forEach(existing -> existing.setIsPrimary(false));
    }

    private PhotoResponse toResponse(Photo photo) {
        return PhotoResponse.builder()
                .id(photo.getId())
                .imageUrl(photo.getStorageUrl())
                .displayOrder(photo.getDisplayOrder())
                .isPrimary(photo.getIsPrimary())
                .uploadedAt(photo.getUploadedAt())
                .build();
    }
}
