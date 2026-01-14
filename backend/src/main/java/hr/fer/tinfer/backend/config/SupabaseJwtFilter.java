package hr.fer.tinfer.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import hr.fer.tinfer.backend.service.SupabaseJwtService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SupabaseJwtFilter extends OncePerRequestFilter {

    private final SupabaseJwtService supabaseJwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Also check for token in query param (for SSE/EventSource which can't set
        // headers)
        if (authHeader == null) {
            String tokenParam = request.getParameter("token");
            if (tokenParam != null && !tokenParam.isEmpty()) {
                authHeader = "Bearer " + tokenParam;
            }
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            supabaseJwtService.authenticate(authHeader).ifPresentOrElse(auth -> {
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Authenticated user: {}", auth.getPrincipal());
            }, () -> log.warn("JWT validation failed for incoming request"));
        }

        filterChain.doFilter(request, response);
    }
}
