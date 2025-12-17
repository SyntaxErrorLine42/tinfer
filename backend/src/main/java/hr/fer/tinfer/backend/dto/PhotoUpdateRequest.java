package hr.fer.tinfer.backend.dto;

import lombok.Data;

@Data
public class PhotoUpdateRequest {

    private String base64Data; // Optional - update image data

    private Integer displayOrder;

    private Boolean isPrimary;
}
