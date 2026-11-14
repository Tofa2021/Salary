package com.example.backend.service.auth;

import com.example.backend.auth.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceLogoutTest extends BaseAuthServiceTest {

    @Test
    void logout_Revokes() {
        RefreshToken token = refreshToken("HASHED", 1L,
                LocalDateTime.now(clock).plusDays(1), null);
        when(refreshTokenGenerator.hash("RAW")).thenReturn("HASHED");
        when(refreshTokenRepository.findByTokenHash("HASHED")).thenReturn(Optional.of(token));
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        authService.logout("RAW");

        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void logout_NotFound_NoOp() {
        when(refreshTokenGenerator.hash("RAW")).thenReturn("HASHED");
        when(refreshTokenRepository.findByTokenHash("HASHED")).thenReturn(Optional.empty());

        authService.logout("RAW");

        verify(refreshTokenRepository, never()).save(any());
    }
}
