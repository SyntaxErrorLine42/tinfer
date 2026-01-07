package hr.fer.tinfer.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProfileRequest {

    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String displayName;

    private String bio;

    @Min(value = 1, message = "Year of study must be between 1 and 5")
    @Max(value = 5, message = "Year of study must be between 1 and 5")
    private Integer yearOfStudy;

    private String studentId;

    @NotBlank(message = "Gender is required")
    private String gender; // male, female, non_binary, other

    private String interestedInGender; // male, female, non_binary, everyone
}
