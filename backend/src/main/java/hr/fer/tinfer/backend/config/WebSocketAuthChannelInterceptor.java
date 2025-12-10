package hr.fer.tinfer.backend.config;

import hr.fer.tinfer.backend.service.SupabaseJwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final SupabaseJwtService supabaseJwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = firstNonBlank(
                    accessor.getFirstNativeHeader("Authorization"),
                    accessor.getFirstNativeHeader("authorization"),
                    accessor.getFirstNativeHeader("token"));

            Authentication authentication = supabaseJwtService.authenticate(authHeader)
                    .orElseThrow(() -> new AccessDeniedException("Invalid or missing JWT for WebSocket connection"));

            accessor.setUser(authentication);
            log.debug("WebSocket CONNECT authenticated for user {}", authentication.getPrincipal());
        }

        return message;
    }

    private String firstNonBlank(String... headers) {
        if (headers == null) {
            return null;
        }
        for (String header : headers) {
            if (header != null && !header.isBlank()) {
                return header;
            }
        }
        return null;
    }
}
