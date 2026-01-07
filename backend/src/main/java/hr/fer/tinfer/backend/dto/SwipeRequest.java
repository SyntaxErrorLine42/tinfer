package hr.fer.tinfer.backend.dto;

import hr.fer.tinfer.backend.types.SwipeAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SwipeRequest {

    @NotNull(message = "User ID is required")
    private UUID swipedUserId;

    @NotNull(message = "Action is required")
    private SwipeAction action;
}
