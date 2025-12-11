package hr.fer.tinfer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private String bio;
    private Integer yearOfStudy;
    private String studentId;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
