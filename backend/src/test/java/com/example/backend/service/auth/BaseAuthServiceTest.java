package com.example.backend.service.auth;

import com.example.backend.auth.application.service.AuthService;
import com.example.backend.auth.domain.model.RefreshToken;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import com.example.backend.auth.domain.port.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ExtendWith(MockitoExtension.class)
abstract class BaseAuthServiceTest {

    protected final Clock clock = Clock.fixed(
            LocalDateTime.of(2026, 1, 15, 12, 0).toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC);

    protected final Duration refreshTtl = Duration.ofDays(7);

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected RefreshTokenRepository refreshTokenRepository;

    @Mock
    protected PasswordHasher passwordHasher;

    @Mock
    protected JwtProvider jwtProvider;

    @Mock
    protected RefreshTokenGenerator refreshTokenGenerator;

    protected AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                refreshTokenGenerator,
                refreshTokenRepository,
                passwordHasher,
                jwtProvider,
                clock,
                refreshTtl
        );
    }

    protected User user(Long id, String username, UserRole role) {
        return User.rehydrate(id, username, "HASH", role);
    }

    protected RefreshToken refreshToken(String hash, Long userId,
                                        LocalDateTime expiresAt, LocalDateTime revokedAt) {
        return RefreshToken.rehydrate(
                1L,
                hash,
                userId,
                LocalDateTime.now(clock),
                expiresAt,
                revokedAt
        );
    }
}
