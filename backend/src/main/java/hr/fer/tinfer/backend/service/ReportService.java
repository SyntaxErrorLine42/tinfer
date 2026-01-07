package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.ReportRequest;
import hr.fer.tinfer.backend.model.Profile;
import hr.fer.tinfer.backend.model.Report;
import hr.fer.tinfer.backend.repository.ProfileRepository;
import hr.fer.tinfer.backend.repository.ReportRepository;
import hr.fer.tinfer.backend.types.ReportStatus;
import hr.fer.tinfer.backend.types.SwipeAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final SwipeService swipeService;

    public void reportUser(UUID reporterId, ReportRequest request) {
        if (reporterId.equals(request.getReportedId())) {
            throw new IllegalArgumentException("You cannot report yourself");
        }

        Profile reporter = profileRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Reporter profile not found"));
        Profile reported = profileRepository.findById(request.getReportedId())
                .orElseThrow(() -> new IllegalArgumentException("Reported profile not found"));

        // 1. Create and Save Report
        Report report = new Report();
        report.setReporter(reporter);
        report.setReported(reported);
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setStatus(ReportStatus.PENDING);
        reportRepository.save(report);

        log.info("User {} reported user {} for {}", reporterId, request.getReportedId(), request.getReason());

        // 2. Auto-Pass to prevent seeing the user again
        try {
            swipeService.swipe(reporterId, request.getReportedId(), SwipeAction.PASS);
            log.info("Auto-passed user {} for reporter {}", request.getReportedId(), reporterId);
        } catch (Exception e) {
            log.error("Failed to auto-pass user {} after report", request.getReportedId(), e);
            // Don't fail the report if swipe fails, but log it
        }

        // 3. Send Email
        String emailBody = String.format("""
                New User Report

                Reporter: %s %s (%s)
                Reported User: %s %s (%s)
                Reason: %s
                Description: %s
                Time: %s
                """,
                reporter.getFirstName(), reporter.getLastName(), reporter.getEmail(),
                reported.getFirstName(), reported.getLastName(), reported.getEmail(),
                request.getReason(),
                request.getDescription(),
                LocalDateTime.now());

        emailService.sendEmail("tinfer.app.project@gmail.com", "New User Report: " + request.getReason(), emailBody);
    }
}
