package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.UserActivityLogResponse;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.model.UserActivityLog;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import hr.fer.tinfer.backend.repository.UserActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserActivityLogService {

    private final UserActivityLogRepository activityLogRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public void recordActivity(UUID userId, String activityType, Map<String, Object> metadata) {
        if (userId == null) {
            log.debug("Skipping activity log without userId for type {}", activityType);
            return;
        }

        Optional<Profile> profileOpt = profileRepository.findById(userId);
        if (profileOpt.isEmpty()) {
            log.debug("Skipping activity log for non-existing profile {}", userId);
            return;
        }

        UserActivityLog logEntry = new UserActivityLog();
        logEntry.setUser(profileOpt.get());
        logEntry.setActivityType(activityType);
        logEntry.setMetadata(metadata == null ? Collections.emptyMap() : metadata);

        activityLogRepository.save(logEntry);
    }

    @Transactional(readOnly = true)
    public List<UserActivityLogResponse> getActivityForUser(UUID userId) {
        return activityLogRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UserActivityLogResponse toResponse(UserActivityLog logEntry) {
        return UserActivityLogResponse.builder()
                .id(logEntry.getId())
                .activityType(logEntry.getActivityType())
                .metadata(logEntry.getMetadata())
                .createdAt(logEntry.getCreatedAt())
                .build();
    }
}
