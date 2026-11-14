package com.example.backend.service.auth;

import com.example.backend.auth.api.dto.auth.AuthResult;
import com.example.backend.auth.domain.exception.TokenRevokedException;
import com.example.backend.auth.domain.model.RefreshToken;
import com.example.backend.auth.domain.model.User;
import com.example.backend.auth.domain.model.UserRole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceRefreshTest extends BaseAuthServiceTest {

    @Test
    void refresh_Valid_RotatesAndReturnsNewTokens() {
        RefreshToken token = refreshToken("HASHED", 1L,
                LocalDateTime.now(clock).plusDays(1), null);
        User u = user(1L, "user", UserRole.HR);
        when(refreshTokenGenerator.hash("RAW")).thenReturn("HASHED");
        when(refreshTokenRepository.findByTokenHash("HASHED")).thenReturn(Optional.of(token));
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(jwtProvider.generateAccess(u)).thenReturn("NEW_ACCESS");
        when(refreshTokenGenerator.generateRaw()).thenReturn("NEW_RAW");
        when(refreshTokenGenerator.hash("NEW_RAW")).thenReturn("NEW_HASH");
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AuthResult result = authService.refresh("RAW");

        assertThat(result.accessToken()).isEqualTo("NEW_ACCESS");
        assertThat(result.refreshToken()).isEqualTo("NEW_RAW");
        assertThat(token.isRevoked()).isTrue();
        verify(refreshTokenRepository, times(2)).save(any());
    }

    @Test
    void refresh_Revoked_RevokesAllAndThrows() {
        RefreshToken token = refreshToken("HASHED", 1L,
                LocalDateTime.now(clock).plusDays(1),
                LocalDateTime.now(clock).minusHours(1));
        when(refreshTokenGenerator.hash("RAW")).thenReturn("HASHED");
        when(refreshTokenRepository.findByTokenHash("HASHED")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.refresh("RAW"))
                .isInstanceOf(TokenRevokedException.class);

        verify(refreshTokenRepository).revokeAllByUserId(eq(1L), any());
    }

    @Test
    void refresh_Expired_Throws() {
        RefreshToken token = refreshToken("HASHED", 1L,
                LocalDateTime.now(clock).minusDays(1), null);
        when(refreshTokenGenerator.hash("RAW")).thenReturn("HASHED");
        when(refreshTokenRepository.findByTokenHash("HASHED")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.refresh("RAW"))
                .isInstanceOf(TokenRevokedException.class);
    }

    @Test
    void refresh_NotFound_Throws() {
        when(refreshTokenGenerator.hash("RAW")).thenReturn("HASHED");
        when(refreshTokenRepository.findByTokenHash("HASHED")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("RAW"))
                .isInstanceOf(TokenRevokedException.class);
    }
}
