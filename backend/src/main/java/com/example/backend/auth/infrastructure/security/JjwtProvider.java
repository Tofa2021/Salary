package com.example.backend.auth.infrastructure.security;

import com.example.backend.auth.domain.exception.TokenRevokedException;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.port.JwtProvider;
import com.example.backend.auth.infrastructure.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JjwtProvider implements JwtProvider {
    private final SecretKey key;
    private final long accessTtlMillis;

    public JjwtProvider(AuthProperties props) {
        String secret = props.getJwt().getSecret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("auth.jwt.secret must be >= 32 chars");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlMillis = props.getJwt().getAccessTtl().toMillis();
    }

    @Override
    public String generateAccess(User user) {
        Instant now = Instant.now();
        String role = user.getRole().name();

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("login", user.getUsername())
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTtlMillis)))
                .signWith(key)
                .compact();
    }

    @Override
    public Long validateAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenRevokedException();
        }
    }
}
