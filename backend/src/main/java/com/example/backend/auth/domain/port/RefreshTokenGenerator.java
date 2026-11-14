package com.example.backend.auth.domain.port;

public interface RefreshTokenGenerator {
    String hash(String refreshToken);

    String generateRaw();
}
