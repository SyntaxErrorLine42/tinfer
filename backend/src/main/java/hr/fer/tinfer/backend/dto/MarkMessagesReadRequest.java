package hr.fer.tinfer.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarkMessagesReadRequest {

    @NotNull
    private Long lastReadMessageId;
}
