package hr.fer.tinfer.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PhotoUpdateRequest {

    @Size(max = 500)
    private String url;

    private Integer displayOrder;

    private Boolean isPrimary;
}
