package hr.fer.tinfer.backend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupabaseJwtService {

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;

    public Optional<Authentication> authenticate(String rawHeader) {
        if (rawHeader == null || rawHeader.isBlank()) {
            return Optional.empty();
        }

        try {
            String token = rawHeader.startsWith("Bearer ") ? rawHeader.substring(7) : rawHeader;

            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            DecodedJWT jwt = JWT.require(algorithm)
                    .build()
                    .verify(token);

            UUID userId = UUID.fromString(jwt.getSubject());
            String email = jwt.getClaim("email").asString();

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
            authentication.setDetails(email);

            return Optional.of(authentication);
        } catch (Exception ex) {
            log.warn("Failed to authenticate Supabase JWT: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
