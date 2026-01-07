package hr.fer.tinfer.backend.dto;

import hr.fer.tinfer.backend.types.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReportRequest {

    @NotNull
    private UUID reportedId;

    @NotNull
    private ReportReason reason;

    private String description;
}
