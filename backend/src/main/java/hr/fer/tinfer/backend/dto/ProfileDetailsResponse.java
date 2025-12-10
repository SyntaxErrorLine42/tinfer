package hr.fer.tinfer.backend.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class ProfileDetailsResponse {
    UUID id;
    String email;
    String firstName;
    String lastName;
    String displayName;
    String bio;
    Integer yearOfStudy;
    String studentId;
    Boolean isVerified;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<String> interests;
    List<String> departments;
    List<PhotoResponse> photos;
}
