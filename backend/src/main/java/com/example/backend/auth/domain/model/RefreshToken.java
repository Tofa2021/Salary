package com.example.backend.auth.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefreshToken {
    private Long id;
    private String token;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;

    public static RefreshToken create(String token, Long userId, LocalDateTime createdAt, LocalDateTime expiresAt) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.token = token;
        refreshToken.userId = userId;
        refreshToken.createdAt = createdAt;
        refreshToken.expiresAt = expiresAt;
        return refreshToken;
    }

    public static RefreshToken rehydrate(Long id, String token, Long userId, LocalDateTime createdAt, LocalDateTime expiresAt, LocalDateTime revokedAt) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.id = id;
        refreshToken.token = token;
        refreshToken.userId = userId;
        refreshToken.createdAt = createdAt;
        refreshToken.expiresAt = expiresAt;
        refreshToken.revokedAt = revokedAt;
        return refreshToken;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired(LocalDateTime now) {
        return expiresAt.isBefore(now);
    }

    public void revoke(LocalDateTime now) {
        this.revokedAt = now;
    }
}
