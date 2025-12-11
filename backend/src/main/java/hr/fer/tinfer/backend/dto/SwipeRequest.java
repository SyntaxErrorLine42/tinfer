package hr.fer.tinfer.backend.dto;

import hr.fer.tinfer.backend.types.SwipeAction;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SwipeRequest {

    @NotNull(message = "ID korisnika je obavezan")
    private UUID swipedUserId;

    @NotNull(message = "Akcija je obavezna")
    private SwipeAction action;
}
