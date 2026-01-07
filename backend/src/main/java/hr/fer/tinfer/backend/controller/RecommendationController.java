package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.dto.ProfileRecommendation;
import hr.fer.tinfer.backend.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Recommendations", description = "Profile recommendations based on interests and activities")
@SecurityRequirement(name = "Bearer Authentication")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    @Operation(summary = "Get a list of recommended profiles")
    public ResponseEntity<List<ProfileRecommendation>> getRecommendations(
            Authentication authentication,
            @Parameter(description = "Number of profiles to fetch", example = "20") @RequestParam(defaultValue = "25") @Min(1) @Max(100) int limit) {

        UUID userId = (UUID) authentication.getPrincipal();
        List<ProfileRecommendation> recommendations = recommendationService.getRecommendations(userId, limit);
        return ResponseEntity.ok(recommendations);
    }
}
