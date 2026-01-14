package hr.fer.tinfer.backend.service;

import hr.fer.tinfer.backend.dto.MatchNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing Server-Sent Events (SSE) connections for real-time match notifications.
 * Each user maintains a single SSE connection to receive instant match notifications.
 */
@Service
@Slf4j
public class MatchNotificationService {

    // Map of userId -> SSE emitter for active connections
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Register a new SSE connection for a user.
     * Replaces any existing connection for the same user.
     */
    public SseEmitter subscribe(UUID userId) {
        // 30 minute timeout (0 = no timeout, but we want some cleanup)
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        // Remove old emitter if exists
        SseEmitter oldEmitter = emitters.put(userId, emitter);
        if (oldEmitter != null) {
            oldEmitter.complete();
        }

        // Cleanup on completion/timeout/error
        emitter.onCompletion(() -> {
            log.debug("SSE connection completed for user: {}", userId);
            emitters.remove(userId, emitter);
        });
        emitter.onTimeout(() -> {
            log.debug("SSE connection timed out for user: {}", userId);
            emitters.remove(userId, emitter);
        });
        emitter.onError(e -> {
            log.debug("SSE connection error for user: {}", userId);
            emitters.remove(userId, emitter);
        });

        log.info("User {} subscribed to match notifications", userId);

        // Send initial connection event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to match notifications"));
        } catch (IOException e) {
            log.warn("Failed to send initial SSE event to user {}", userId);
        }

        return emitter;
    }

    /**
     * Send match notification to a specific user.
     */
    public void notifyMatch(UUID userId, MatchNotification notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.debug("No active SSE connection for user: {}", userId);
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name("match")
                    .data(notification));
            log.info("Match notification sent to user: {}", userId);
        } catch (IOException e) {
            log.warn("Failed to send match notification to user {}, removing emitter", userId);
            emitters.remove(userId, emitter);
        }
    }

    /**
     * Send match notification to both users involved in the match.
     */
    public void notifyBothUsers(UUID user1Id, UUID user2Id, MatchNotification notification1, MatchNotification notification2) {
        notifyMatch(user1Id, notification1);
        notifyMatch(user2Id, notification2);
    }

    /**
     * Unsubscribe a user from notifications.
     */
    public void unsubscribe(UUID userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            emitter.complete();
            log.info("User {} unsubscribed from match notifications", userId);
        }
    }

    /**
     * Check if a user has an active SSE connection.
     */
    public boolean isConnected(UUID userId) {
        return emitters.containsKey(userId);
    }

    /**
     * Get count of active connections (for monitoring).
     */
    public int getActiveConnectionCount() {
        return emitters.size();
    }
}
