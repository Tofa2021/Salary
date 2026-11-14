package com.example.backend.auth.domain.port;

import com.example.backend.auth.domain.model.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByTokenHash(String hash);

    void revokeAllByUserId(Long userId, LocalDateTime now);

    RefreshToken save(RefreshToken token);
}
