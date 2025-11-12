package hr.fer.tinfer.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProfileRequest {

    @Email
    @NotBlank(message = "Email je obavezan")
    private String email;

    @NotBlank(message = "Ime je obavezno")
    private String firstName;

    @NotBlank(message = "Prezime je obavezno")
    private String lastName;

    private String displayName;

    private String bio;

    @Min(value = 1, message = "Godina studija mora biti između 1 i 5")
    @Max(value = 5, message = "Godina studija mora biti između 1 i 5")
    private Integer yearOfStudy;

    private String studentId;
}
