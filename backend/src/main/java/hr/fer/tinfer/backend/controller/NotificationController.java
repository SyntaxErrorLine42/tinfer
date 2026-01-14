package hr.fer.tinfer.backend.controller;

import hr.fer.tinfer.backend.service.MatchNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * Controller for Server-Sent Events (SSE) notifications.
 * Provides real-time match notifications to connected clients.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final MatchNotificationService matchNotificationService;

    /**
     * Subscribe to real-time match notifications via SSE.
     * Client should keep this connection open to receive instant match alerts.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UUID userId) {
        return matchNotificationService.subscribe(userId);
    }
}
