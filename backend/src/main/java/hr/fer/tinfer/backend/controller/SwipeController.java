package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.dto.SwipeRequest;
import hr.fer.tinfer.backend.dto.SwipeResponse;
import hr.fer.tinfer.backend.service.SwipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
@Tag(name = "Swipes", description = "Swipe actions and matches")
@SecurityRequirement(name = "Bearer Authentication")
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping
    @Operation(summary = "Send a swipe action for a given profile")
    public ResponseEntity<SwipeResponse> swipe(
            @Valid @RequestBody SwipeRequest request,
            Authentication authentication) {

        UUID swiperId = (UUID) authentication.getPrincipal();
        SwipeResponse response = swipeService.swipe(swiperId, request.getSwipedUserId(), request.getAction());
        return ResponseEntity.ok(response);
    }
}
