package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.model.Interest;
import hr.fer.tinfer.backend.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Interests", description = "Interest management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class InterestController {

    private final InterestService interestService;

    @GetMapping
    @Operation(summary = "Get all interests")
    public ResponseEntity<List<Interest>> getAllInterests() {
        log.debug("Fetching all interests");
        return ResponseEntity.ok(interestService.getAllInterests());
    }

    @GetMapping("/search")
    @Operation(summary = "Search interests by name")
    public ResponseEntity<List<Interest>> searchInterests(
            @RequestParam(required = false) String q) {
        log.debug("Searching interests with query: {}", q);
        return ResponseEntity.ok(interestService.searchInterests(q));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get interests by category")
    public ResponseEntity<List<Interest>> getInterestsByCategory(
            @PathVariable String category) {
        log.debug("Fetching interests for category: {}", category);
        return ResponseEntity.ok(interestService.getInterestsByCategory(category));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's interests")
    public ResponseEntity<Set<Interest>> getMyInterests(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        log.debug("Fetching interests for user: {}", userId);
        return ResponseEntity.ok(interestService.getUserInterests(userId));
    }

    @PostMapping("/me")
    @Operation(summary = "Add interests to current user's profile")
    public ResponseEntity<Void> addInterests(
            Authentication authentication,
            @RequestBody Set<String> interestNames) {
        UUID userId = (UUID) authentication.getPrincipal();
        log.info("Adding interests to user {}: {}", userId, interestNames);
        interestService.addInterestsToProfile(userId, interestNames);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me")
    @Operation(summary = "Set user's interests (replace all)")
    public ResponseEntity<Void> setInterests(
            Authentication authentication,
            @RequestBody Set<String> interestNames) {
        UUID userId = (UUID) authentication.getPrincipal();
        log.info("Setting interests for user {}: {}", userId, interestNames);
        interestService.setProfileInterests(userId, interestNames);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me/{interestName}")
    @Operation(summary = "Remove interest from current user's profile")
    public ResponseEntity<Void> removeInterest(
            Authentication authentication,
            @PathVariable String interestName) {
        UUID userId = (UUID) authentication.getPrincipal();
        log.info("Removing interest '{}' from user {}", interestName, userId);
        interestService.removeInterestFromProfile(userId, interestName);
        return ResponseEntity.ok().build();
    }
}
