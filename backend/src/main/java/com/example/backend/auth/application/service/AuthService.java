package com.example.backend.auth.application.service;

import com.example.backend.auth.api.dto.auth.AuthResult;
import com.example.backend.auth.api.dto.auth.LoginCommand;
import com.example.backend.auth.domain.exception.InvalidCredentialsException;
import com.example.backend.auth.domain.exception.TokenRevokedException;
import com.example.backend.auth.domain.model.RefreshToken;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.port.*;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHasher passwordEncoder;
    private final JwtProvider jwtProvider;
    private final Clock clock;
    private final Duration refreshTtl;

    public AuthResult login(LoginCommand request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String access = jwtProvider.generateAccess(user);
        String refresh = issueRefreshToken(user.getId());
        return new AuthResult(access, refresh, user.getUsername(), user.getRole());
    }

    public AuthResult refresh(String refreshToken) {
        String hash = refreshTokenGenerator.hash(refreshToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(TokenRevokedException::new);

        LocalDateTime now = LocalDateTime.now(clock);

        if (token.isRevoked()) {
            refreshTokenRepository.revokeAllByUserId(token.getUserId(), now);
            throw new TokenRevokedException();
        }

        if (token.isExpired(now)) {
            throw new TokenRevokedException();
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(InvalidCredentialsException::new);

        token.revoke(now);
        refreshTokenRepository.save(token);

        String newAccess = jwtProvider.generateAccess(user);
        String newRefreshRaw = issueRefreshToken(user.getId());

        return new AuthResult(newAccess, newRefreshRaw, user.getUsername(), user.getRole());
    }

    public void logout(String refreshToken) {
        String hash = refreshTokenGenerator.hash(refreshToken);
        refreshTokenRepository.findByTokenHash(hash)
                .ifPresent(token -> {
                    LocalDateTime now = LocalDateTime.now(clock);
                    token.revoke(now);
                    refreshTokenRepository.save(token);
                });
    }

    private String issueRefreshToken(Long userId) {
        String raw = refreshTokenGenerator.generateRaw();
        String hash = refreshTokenGenerator.hash(raw);

        LocalDateTime now = LocalDateTime.now(clock);
        RefreshToken token = RefreshToken.create(
                hash,
                userId,
                now,
                now.plus(refreshTtl)
        );
        refreshTokenRepository.save(token);
        return raw;
    }
}
