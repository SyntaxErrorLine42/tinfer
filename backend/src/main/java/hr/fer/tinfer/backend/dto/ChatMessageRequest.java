package hr.fer.tinfer.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatMessageRequest {

    @NotBlank(message = "Chat message content is required")
    private String content;

    @Size(max = 500)
    private String attachmentUrl;
}
