package hr.fer.tinfer.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PhotoResponse {
    private Long id;
    private String base64Data; // Base64 encoded image
    private Integer displayOrder;
    private Boolean isPrimary;
    private LocalDateTime uploadedAt;
}
