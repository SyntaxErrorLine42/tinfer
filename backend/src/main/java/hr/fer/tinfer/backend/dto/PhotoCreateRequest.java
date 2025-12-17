package hr.fer.tinfer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhotoCreateRequest {

    @NotBlank
    private String base64Data; // Base64 encoded image

    private Integer displayOrder;

    private Boolean isPrimary;
}
