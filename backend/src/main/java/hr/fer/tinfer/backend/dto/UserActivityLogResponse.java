package hr.fer.tinfer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

@Value
@Builder
@AllArgsConstructor
public class UserActivityLogResponse {
    Long id;
    String activityType;
    Map<String, Object> metadata;
    LocalDateTime createdAt;
}
