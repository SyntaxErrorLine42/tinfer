package hr.fer.tinfer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PhotoCreateRequest {

    @NotBlank
    @Size(max = 500)
    private String url;

    private Integer displayOrder;

    private Boolean isPrimary;
}
