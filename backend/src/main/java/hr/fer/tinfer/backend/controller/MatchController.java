package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.dto.MatchSummary;
import hr.fer.tinfer.backend.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
@Tag(name = "Matches", description = "Pregled podudaranja i razgovora")
@SecurityRequirement(name = "Bearer Authentication")
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    @Operation(summary = "Dohvati sve match-eve za trenutnog korisnika")
    public ResponseEntity<List<MatchSummary>> getMatches(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<MatchSummary> matches = matchService.getMatches(userId);
        return ResponseEntity.ok(matches);
    }
}
