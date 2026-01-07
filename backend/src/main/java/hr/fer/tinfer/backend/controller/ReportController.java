package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.dto.ReportRequest;
import hr.fer.tinfer.backend.service.ReportService;
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
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Reporting users")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @Operation(summary = "Report a user")
    public ResponseEntity<Void> reportUser(
            @Valid @RequestBody ReportRequest request,
            Authentication authentication) {

        UUID reporterId = (UUID) authentication.getPrincipal();
        reportService.reportUser(reporterId, request);
        return ResponseEntity.ok().build();
    }
}
